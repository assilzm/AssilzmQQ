package qq

import http.HttpClient
import utils.RegexUtils

/**
 *
 * @author: Assilzm
 * create: 16:48.
 * description:
 */
class Login {

    static String client_id = new Random(111111).nextInt(888888)
    static String ptwebqq
    static String psessionid
    static String appid
    static String vfwebqq
    static String qrcode_path = "codeImage.png"
    static String username
    static String account
    //String qqNum,String password
    void login() {
        HttpClient httpClient = new HttpClient()
        String initPage = httpClient.get("http://w.qq.com/login.html")
        String initUrl = RegexUtils.getRegexValue(initPage, /\.getElementById\("fr"\)\.src\s*=\s*"([^"]+)/)
        String initPageContent = httpClient.get(initUrl + "0")
        appid = RegexUtils.getRegexValue(initPageContent, /var g_appid =encodeURIComponent\("(\d+)"\);/)
        String sig = RegexUtils.getRegexValue(initPageContent, /var g_login_sig=encodeURIComponent\("(.*?)"\);/)
        String js_ver = RegexUtils.getRegexValue(initPageContent, /var g_pt_version=encodeURIComponent\("(\d+)"\);/)
        String mibao_css = RegexUtils.getRegexValue(initPageContent, /var g_mibao_css=encodeURIComponent\("(.+?)"\);/)
        File file = new File(qrcode_path)
        httpClient.getFile("https://ssl.ptlogin2.qq.com/ptqrshow?appid=${appid}&e=0&l=L&s=8&d=72&v=4", file)
        Long startTime = System.currentTimeMillis()
        sleep(2000)
        println "https://ssl.ptlogin2.qq.com/ptqrlogin?webqq_type=10&remember_uin=1&login2qq=1&aid=${appid}&u1=http%3A%2F%2Fw.qq.com%2Fproxy.html%3Flogin2qq%3D1%26webqq_type%3D10&ptredirect=0&ptlang=2052&daid=164&from_ui=1&pttype=1&dumy=&fp=loginerroralert&action=0-0-${System.currentTimeMillis() - startTime}&mibao_css=${mibao_css}&t=undefined&g=1&js_type=0&js_ver=${js_ver}&login_sig=${sig}"

        String loginPage = httpClient.get("https://ssl.ptlogin2.qq.com/ptqrlogin?webqq_type=10&remember_uin=1&login2qq=1&aid=${appid}&u1=http%3A%2F%2Fw.qq.com%2Fproxy.html%3Flogin2qq%3D1%26webqq_type%3D10&ptredirect=0&ptlang=2052&daid=164&from_ui=1&pttype=1&dumy=&fp=loginerroralert&action=0-0-${System.currentTimeMillis() - startTime}&mibao_css=${mibao_css}&t=undefined&g=1&js_type=0&js_ver=${js_ver}&login_sig=${sig}")
        println loginPage
        httpClient.close()

    }
}
