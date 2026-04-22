import SparkMD5 from 'spark-md5'

/**
 * 获取浏览器指纹
 * 基于浏览器特征生成唯一标识
 */
export function getBrowserFingerprint(): string {
  const navigator = window.navigator
  const screen = window.screen
  
  const components = [
    // User Agent
    navigator.userAgent,
    // 语言
    navigator.language,
    // 平台
    navigator.platform,
    // 设备内存
    (navigator as any).deviceMemory || '',
    // CPU核心数
    navigator.hardwareConcurrency?.toString() || '',
    // 屏幕分辨率
    screen.width + 'x' + screen.height,
    // 屏幕色深
    screen.colorDepth,
    // 时区
    new Date().getTimezoneOffset().toString(),
    // Canvas指纹
    getCanvasFingerprint(),
  ]
  
  const fingerprint = components.join('###')
  return SparkMD5.hash(fingerprint)
}

/**
 * 获取Canvas指纹
 */
function getCanvasFingerprint(): string {
  try {
    const canvas = document.createElement('canvas')
    const ctx = canvas.getContext('2d')
    if (!ctx) return ''
    
    canvas.width = 200
    canvas.height = 50
    
    // 绘制文本
    ctx.textBaseline = 'top'
    ctx.font = '14px Arial'
    ctx.fillStyle = '#f60'
    ctx.fillRect(125, 1, 62, 20)
    ctx.fillStyle = '#069'
    ctx.fillText('Browser Fingerprint', 2, 15)
    ctx.fillStyle = 'rgba(102, 204, 0, 0.7)'
    ctx.fillText('Browser Fingerprint', 4, 17)
    
    return canvas.toDataURL().slice(-50)
  } catch (e) {
    return ''
  }
}
