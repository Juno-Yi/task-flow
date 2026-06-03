/**
 * API 加密/解密工具
 * 
 * 加密协议说明：
 * - 响应加密：服务端用 RSA 私钥加密 AES 密钥 → 前端用公钥解密
 * - 请求加密：前端用 RSA 公钥加密 AES 密钥 → 服务端用私钥解密
 * - 数据格式：`encryptedAesKey.iv.encryptedData`（Base64）
 * - RSA 算法：RSA/ECB/PKCS1Padding
 * - AES 算法：AES-GCM (256-bit key, 12-byte IV, 128-bit tag)
 * 
 * @module utils/http/crypto
 */

import forge from 'node-forge'

/**
 * 获取 RSA 公钥 PEM 格式
 */
function getPublicKeyPem(): string {
  const key = import.meta.env.VITE_API_ENCRYPT_PUBLIC_KEY
  if (!key) {
    throw new Error('未配置 API 加密公钥 (VITE_API_ENCRYPT_PUBLIC_KEY)')
  }
  // 将单行 Base64 转换为每 64 字符换行的 PEM 格式
  const formattedKey = key.match(/.{1,64}/g)?.join('\n') || key
  return `-----BEGIN PUBLIC KEY-----\n${formattedKey}\n-----END PUBLIC KEY-----`
}

// 延迟解析公钥（避免环境变量未加载时报错）
let _publicKey: forge.pki.rsa.PublicKey | null = null
function getPublicKey(): forge.pki.rsa.PublicKey {
  if (!_publicKey) {
    _publicKey = forge.pki.publicKeyFromPem(getPublicKeyPem())
  }
  return _publicKey
}

/**
 * 去除 PKCS1 v1.5 填充
 */
function removePkcs1Padding(data: string): string {
  let i = 0
  if (data.charCodeAt(i++) !== 0x00) throw new Error('Invalid padding')
  if (data.charCodeAt(i++) !== 0x01) throw new Error('Invalid padding') // 私钥加密用 0x01
  while (data.charCodeAt(i) === 0xff) i++
  if (data.charCodeAt(i++) !== 0x00) throw new Error('Invalid padding')
  return data.substring(i)
}

/**
 * 解密响应（服务端私钥加密 → 前端公钥解密）
 * @param encryptedText 加密的文本
 * @returns 解密后的明文
 */
export function decryptResponse(encryptedText: string): string {
  // 去除可能的空白字符
  const trimmedText = encryptedText.trim()
  
  const parts = trimmedText.split('.')
  if (parts.length !== 3) {
    console.error('加密数据格式错误，期望3段，实际:', parts.length, '数据预览:', trimmedText.substring(0, 100))
    throw new Error('加密数据格式错误')
  }

  // Base64 解码
  const encryptedAesKey = forge.util.decode64(parts[0])
  const iv = forge.util.decode64(parts[1])
  const encryptedData = forge.util.decode64(parts[2])

  // 使用 RSA 公钥解密 AES 密钥
  // 注意：这里是"公钥解密私钥加密的数据"，需要用 raw RSA
  const publicKey = getPublicKey()
  const n = publicKey.n
  const e = publicKey.e
  const encryptedBigInt = new forge.jsbn.BigInteger(forge.util.bytesToHex(encryptedAesKey), 16)
  const decryptedBigInt = encryptedBigInt.modPow(e, n)
  let decryptedHex = decryptedBigInt.toString(16)
  // 根据密钥长度补齐前导零（1024位密钥=256字符，2048位密钥=512字符）
  const keyLength = Math.ceil(n.bitLength() / 4)
  while (decryptedHex.length < keyLength) decryptedHex = '0' + decryptedHex
  const decryptedBytes = forge.util.hexToBytes(decryptedHex)

  // 去除 PKCS1 填充
  const aesKeyBytes = removePkcs1Padding(decryptedBytes)

  // 使用 AES-GCM 解密数据
  const decipher = forge.cipher.createDecipher('AES-GCM', aesKeyBytes)
  decipher.start({
    iv: iv,
    tagLength: 128,
    tag: forge.util.createBuffer(encryptedData.slice(-16))
  })
  decipher.update(forge.util.createBuffer(encryptedData.slice(0, -16)))
  const success = decipher.finish()

  if (!success) throw new Error('AES 解密失败')
  return decipher.output.toString()
}

/**
 * 加密请求（前端公钥加密 → 服务端私钥解密）
 * @param plainText 明文
 * @returns 加密后的文本
 */
export function encryptRequest(plainText: string): string {
  // 生成随机 AES 密钥 (32 bytes = 256 bits)
  const aesKey = forge.random.getBytesSync(32)

  // 生成随机 IV (12 bytes for GCM)
  const iv = forge.random.getBytesSync(12)

  // 使用 AES-GCM 加密数据
  const cipher = forge.cipher.createCipher('AES-GCM', aesKey)
  cipher.start({ iv: iv, tagLength: 128 })
  cipher.update(forge.util.createBuffer(plainText, 'utf8'))
  cipher.finish()
  const encryptedData = cipher.output.getBytes() + cipher.mode.tag.getBytes()

  // 使用 RSA 公钥加密 AES 密钥
  const publicKey = getPublicKey()
  const encryptedAesKey = publicKey.encrypt(aesKey, 'RSAES-PKCS1-V1_5')

  // 组合结果
  return [
    forge.util.encode64(encryptedAesKey),
    forge.util.encode64(iv),
    forge.util.encode64(encryptedData)
  ].join('.')
}

/**
 * 检查是否启用 API 加密
 */
export function isApiEncryptEnabled(): boolean {
  return import.meta.env.VITE_API_ENCRYPT === 'true'
}
