package http

import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.Param
import com.ning.http.client.Request
import com.ning.http.client.Response
import com.ning.http.client.cookie.Cookie
import com.ning.http.client.resumable.ResumableAsyncHandler
import com.ning.http.client.resumable.ResumableListener

import java.nio.ByteBuffer

/**
 *
 * @author: Assilzm
 * create: 16:04.
 * description:
 */
class HttpClient {

    final static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36"

    List<Cookie> cookies = new ArrayList<>()

    String referer = null

    Map<String, String> headerToAdd = [:]


    AsyncHttpClient client

    HttpClient() {
        client = new AsyncHttpClient()
    }

    String getAndReturnBody(String url) {
        get(url).responseBody
    }


    Response get(String url) {
        getResponse("GET", url)
    }

    String postAndReturnBody(String url, Map<String, String> params) {
        post(url, params).responseBody
    }


    Response post(String url, Map<String, String> params) {
        post(url, mapToParamList(params))
    }

    Response post(String url, List<Param> params) {
        getResponse("POST", url, params)
    }

    Response getResponse(String type, String urlString, List<Param> params = null, isSyncDomainCookies = true) {
        URL url = new URL(urlString)
        println url
        if (isSyncDomainCookies)
            cookies.removeAll {
                println it
                !url.host.endsWith(it.domain)
            }
        println cookies
        Request request
        switch (type) {
            case "GET":
                request = client.prepareGet(urlString)
                break
            case "POST":
                request = client.preparePost(urlString)
                break
            default:
                throw new Exception("unknodw method")
        }
        request = addHeader(request.addHeader("User-Agent", USER_AGENT).addHeader("Referer", referer).addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8").addHeader("Accept-Language","zh-cn,zh;q=0.8").addHeader("Connection","keep-alive").addHeader("Accept","*/*").addQueryParams(params))
        cookies.each {
            request = request.addOrReplaceCookie(it)
        }
        def response = request.execute().get()
        println response.headers
        println response.cookies
        syncCookies(response.getCookies())
        println "after sync:${cookies.join("\r\n")}"
        response
    }


    AsyncHttpClient.BoundRequestBuilder addHeader(AsyncHttpClient.BoundRequestBuilder requestBuilder) {
        headerToAdd.each { key, value ->
            requestBuilder.addHeader(key, value)
        }
        headerToAdd.clear()
        return requestBuilder
    }

    void syncCookies(List<Cookie> cookieToSet) {
        cookieToSet.each {
            if (!it.value.trim().empty)
                setCookie(it)
        }

    }

    void setCookie(Cookie cookie) {
        boolean hasSync = false
        for (int i in 0..<cookies.size()) {
            if (cookie.name.equals(cookies[i].name) && cookie.domain.equals(cookies[i].domain)) {
                cookies[i] = cookie
                hasSync = true
            }
        }
        if (!hasSync)
            cookies.add(cookie)
    }


    void getFile(String url, File fileToStore) {
        if (fileToStore.exists())
            fileToStore.delete()
        final RandomAccessFile file = new RandomAccessFile(fileToStore, "rw")
        ResumableAsyncHandler a = new ResumableAsyncHandler()
        a.setResumableListener(new ResumableListener() {

            public void onBytesReceived(ByteBuffer byteBuffer) throws IOException {
                file.seek(file.length())
                file.write(byteBuffer.array())
            }

            public void onAllBytesReceived() {
                file.close()
            }

            public long length() {
                return file.length()
            }
        })
        def request = client.prepareGet(url).addHeader("User-Agent", USER_AGENT).addHeader("Accept", "*/*")

        cookies.each {
            request = request.addOrReplaceCookie(it)
        }
        Response response = request.execute(a).get()
        syncCookies(response.getCookies())

    }

    void clearCookies() {
        cookies.clear()
    }


    String getCookie(String name) {
        for (cookie in cookies) {
            if (cookie.name == name) {
                return cookie.value
            }
        }
        return null
    }

    void getFile(String url, String fileToPath) {
        getFile(url, new File(fileToPath))
    }


    static List<Param> mapToParamList(Map<String, String> param) {
        List<Param> paramList = new ArrayList<>()
        param.each { key, value ->
            paramList.add(new Param(key, value))
        }
        return paramList
    }

    void close() {
        client.close()
    }

}
