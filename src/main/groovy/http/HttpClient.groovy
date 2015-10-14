package http

import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.Param
import com.ning.http.client.cookie.Cookie
import com.ning.http.client.cookie.CookieDecoder
import com.ning.http.client.resumable.ResumableAsyncHandler
import com.ning.http.client.resumable.ResumableListener

import javax.xml.ws.AsyncHandler
import java.nio.ByteBuffer

/**
 *
 * @author: Assilzm
 * create: 16:04.
 * description:
 */
class HttpClient {

    final static String USER_AGENET = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:41.0) Gecko/20100101 Firefox/41.0"

    List<Cookie> cookies = new ArrayList<>()

    String referrer = null

    Map<String,String> headerToAdd = [:]


    AsyncHttpClient client

    HttpClient() {
        client = new AsyncHttpClient()

    }

    String get(String url) {
        println url
        getBody(client.prepareGet(url))

    }

    String post(String url, Map<String, String> params) {
        getBody(client.preparePost(url), mapToParamList(params))
    }

    String post(String url, List<Param> params) {
        getBody(client.preparePost(url), params)
    }

    String getBody(AsyncHttpClient.BoundRequestBuilder request, List<Param> params = null, AsyncHandler asyncHandler = null) {
        println this.cookies
        def response = addHeader(request.setFollowRedirects(true).addHeader("USER-AGENET", USER_AGENET).addHeader("Referrer", referrer).addHeader("Accept", "*/*").addQueryParams(params).setCookies(cookies)).execute().get()
        println response.headers
        println response.cookies
        syncCookies(response.headers.get("Set-Cookie"))
        response.responseBody
    }

    AsyncHttpClient.BoundRequestBuilder addHeader(AsyncHttpClient.BoundRequestBuilder requestBuilder) {
        headerToAdd.each { key, value ->
            requestBuilder.addHeader(key, value)
        }

        headerToAdd.clear()
        return requestBuilder
    }

    void syncCookies(List<String> setCookie) {
        setCookie.each { cookieString ->
            if (cookieString) {
                cookieString.split(";,").each {
                    Cookie cookie = CookieDecoder.decode(it)
                    if (!(cookie in cookies))
                        cookies.add(cookie)
                }
            }
        }
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
        def response = client.prepareGet(url).addHeader("USER-AGENET", USER_AGENET).addHeader("Referrer", referrer).addHeader("Accept", "*/*").setCookies(cookies).execute(a).get()
        syncCookies(response.headers.get("Set-Cookie"))
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
