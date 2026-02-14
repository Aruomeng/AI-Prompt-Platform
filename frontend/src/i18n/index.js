// i18n 国际化配置
import zhCN from './zh-CN'
import enUS from './en-US'

// 支持的语言列表
const messages = {
  'zh-CN': zhCN,
  'en-US': enUS,
}

// 获取用户的语言偏好
function getLocale() {
  // 首先检查localStorage中的设置
  const savedLocale = localStorage.getItem('locale')
  if (savedLocale && messages[savedLocale]) {
    return savedLocale
  }
  
  // 然后检查浏览器语言
  const browserLocale = navigator.language
  if (messages[browserLocale]) {
    return browserLocale
  }
  
  // 检查浏览器语言的主要部分（如 zh-Hans -> zh-CN）
  const mainLang = browserLocale.split('-')[0]
  for (const locale in messages) {
    if (locale.startsWith(mainLang)) {
      return locale
    }
  }
  
  // 默认返回中文
  return 'zh-CN'
}

// 国际化工具类
class I18n {
  constructor() {
    this.locale = getLocale()
    this.messages = messages
  }
  
  // 获取本地化消息
  t(key, defaultValue = key) {
    const keys = key.split('.')
    let value = this.messages[this.locale]
    
    for (const k of keys) {
      if (value && typeof value === 'object') {
        value = value[k]
      } else {
        return defaultValue
      }
    }
    
    return value || defaultValue
  }
  
  // 获取指定语言的消息
  tc(key, locale = this.locale, defaultValue = key) {
    const keys = key.split('.')
    let value = this.messages[locale]
    
    for (const k of keys) {
      if (value && typeof value === 'object') {
        value = value[k]
      } else {
        return defaultValue
      }
    }
    
    return value || defaultValue
  }
  
  // 设置当前语言
  setLocale(locale) {
    if (this.messages[locale]) {
      this.locale = locale
      localStorage.setItem('locale', locale)
      // 触发语言切换事件
      window.dispatchEvent(new CustomEvent('i18n-change', { detail: { locale } }))
    }
  }
  
  // 获取当前语言
  getLocale() {
    return this.locale
  }
  
  // 获取所有支持的语言
  getLocales() {
    return Object.keys(this.messages)
  }
  
  // 支持插值（如 {name}）
  interpolate(text, values = {}) {
    let result = text
    for (const key in values) {
      const regex = new RegExp(`{${key}}`, 'g')
      result = result.replace(regex, values[key])
    }
    return result
  }
  
  // 获取并插值
  tt(key, values = {}) {
    const message = this.t(key)
    return this.interpolate(message, values)
  }
}

// 创建全局实例
export const i18n = new I18n()

// 导出消息和方法
export default {
  install(app) {
    app.config.globalProperties.$t = (key) => i18n.t(key)
    app.config.globalProperties.$tc = (key, locale) => i18n.tc(key, locale)
    app.config.globalProperties.$i18n = i18n
  },
}
