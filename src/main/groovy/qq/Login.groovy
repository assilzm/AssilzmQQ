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
        String html = httpClient.get("http://w.qq.com/login.html")
        String initUrl = RegexUtils.getRegexValue(html, /\.getElementById\("fr"\)\.src\s*=\s*"([^"]+)/)
        String initPageContent = httpClient.get(initUrl + "0")
        appid = RegexUtils.getRegexValue(initPageContent, /var g_appid =encodeURIComponent\("(\d+)"\);/)
        String sig = RegexUtils.getRegexValue(initPageContent, /var g_login_sig=encodeURIComponent\("(.*?)"\);/)
        String js_ver = RegexUtils.getRegexValue(initPageContent, /var g_pt_version=encodeURIComponent\("(\d+)"\);/)
        String mibao_css = RegexUtils.getRegexValue(initPageContent, /var g_mibao_css=encodeURIComponent\("(.+?)"\);/)
        File file = new File(qrcode_path)
        Long startTime = System.currentTimeMillis()
        httpClient.referrer = "https://ui.ptlogin2.qq.com/cgi-bin/login?daid=164&target=self&style=16&mibao_css=m_webqq&appid=501004106&enable_qlogin=0&no_verifyimg=1&s_url=http%3A%2F%2Fw.qq.com%2Fproxy.html&f_url=loginerroralert&strong_login=1&login_state=10&t=20131024001"
        httpClient.getFile("https://ssl.ptlogin2.qq.com/ptqrshow?appid=${appid}&e=0&l=M&s=8&d=72&v=4&t=0.1196356974542141", file)
        List<String> retList=new ArrayList<>()
        while (1) {
            html = httpClient.get("https://ssl.ptlogin2.qq.com/ptqrlogin?webqq_type=10&remember_uin=1&login2qq=1&aid=${appid}&u1=http%3A%2F%2Fw.qq.com%2Fproxy.html%3Flogin2qq%3D1%26webqq_type%3D10&ptredirect=0&ptlang=2052&daid=164&from_ui=1&pttype=1&dumy=&fp=loginerroralert&action=0-0-${System.currentTimeMillis() - startTime}&mibao_css=${mibao_css}&t=undefined&g=1&js_type=0&js_ver=${js_ver}&login_sig=${sig}&pt_randsalt=0")
            println html
            retList = decodeReturnValue(html)
            if (retList.size() > 1 && retList.get(0) == "0")
                break
            sleep(1000)
        }
        assert retList.size()==6
        username=retList.get(5)
        def mibaoUrl=retList.get(2)
        sleep(1000)
        html=httpClient.get(mibaoUrl)
        println html
//        def url=RegexUtils.getRegexValue(html,/proxy_iframe.src = "(.+?)"/)
//        println url
//        html=httpClient.get(url)
//        println html
//        url=RegexUtils.getRegexValue(html,/location\.href="(.+?)"/)
//        println url
//        html=httpClient.get(url)
//        println html
        ptwebqq=httpClient.getCookie("ptwebqq")
        println ptwebqq

        httpClient.referrer="http://d.web2.qq.com/proxy.html?v=20130916001&callback=1&id=2"
        httpClient.headerToAdd=["json":"1"]
        while(1){
            html=httpClient.post("http://d.web2.qq.com/channel/login2",["r":"{\"ptwebqq\":\"${ptwebqq}\",\"clientid\":${client_id},\"psessionid\":\"${psessionid}\",\"status\":\"online\"}"])
            println html
            sleep(1000)
        }
        httpClient.close()
    }

   static List<String> decodeReturnValue(String loginReturnValue) {
        def ptuiCB = ""
        List<String> returnList = new ArrayList()
        def m = loginReturnValue =~ /ptuiCB\(([^)]+)\)/
        if (m.find()) {
            ptuiCB = m.group(1)
            println ptuiCB
            def ptuiCBList = ptuiCB.split(",")
            ptuiCBList.each {
                returnList.add(it.trim().replaceAll(/^'|'$/,""))
            }
        }
        return returnList
    }
}
