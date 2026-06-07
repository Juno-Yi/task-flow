/**
 * 客户端类型
 */
export enum ClientType {
    WEWORK = 'WEWORK',
    FEISHU = 'FEISHU',
    DINGTALK = 'DINGTALK',
    BROWSER = 'BROWSER'
}

/**
 * 获取客户端类型
 */
export function getClientType(): ClientType {
    const ua = navigator.userAgent.toLowerCase()

    console.log('当前UA:', ua)

    if (ua.includes('wxwork')) {
        return ClientType.WEWORK
    }

    if (
        ua.includes('lark') ||
        ua.includes('feishu')
    ) {
        return ClientType.FEISHU
    }

    if (ua.includes('dingtalk')) {
        return ClientType.DINGTALK
    }
    return ClientType.BROWSER
}