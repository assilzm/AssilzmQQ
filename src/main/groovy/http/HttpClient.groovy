package http

import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.Param
import com.ning.http.client.Response
import com.ning.http.client.cookie.Cookie
import com.ning.http.client.cookie.CookieDecoder
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

    final static String USER_AGENET = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:41.0) Gecko/20100101 Firefox/41.0"

    List<Cookie> cookies = new ArrayList<>()

    String referrer = null

    Map<String, String> headerToAdd = [:]


    AsyncHttpClient client

    HttpClient() {
        client = new AsyncHttpClient()

    }

    String getAndReturnBody(String url) {
        get(url).responseBody
    }


    Response get(String url) {
        println url
        getResponse(client.prepareGet(url))
    }

    String postAndReturnBody(String url, Map<String, String> params) {
        post(url,params).responseBody
    }


    Response post(String url, Map<String, String> params) {
        getResponse(client.preparePost(url), mapToParamList(params))
    }

    Response post(String url, List<Param> params) {
        getResponse(client.preparePost(url), params)
    }

    Response getResponse(AsyncHttpClient.BoundRequestBuilder request, List<Param> params = null) {
        println this.cookies
        params.each {
            println "${it.getName()}=${it.getValue()}"

        }
         request= addHeader(request.addHeader("USER-AGENET", USER_AGENET).addHeader("Referrer", referrer).addHeader("Accept", "*/*").addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8").addHeader("Cache-Control", "no-cache").addQueryParams(params))
        cookies.each {
            request=request.addOrReplaceCookie(it)
        }
        def response=request.execute().get()
        println response.headers
        println response.cookies
        syncCookies(response.headers.get("Set-Cookie"))
        response
    }

    AsyncHttpClient.BoundRequestBuilder addHeader(AsyncHttpClient.BoundRequestBuilder requestBuilder) {
        headerToAdd.each { key, value ->
            requestBuilder.addHeader(key, value)
        }

        headerToAdd.clear()
        return requestBuilder
    }

    void syncCookies(List<String> cookieToSet) {
        cookieToSet.each { cookieString ->
            if (cookieString) {
                cookieString.split(";,").each {
                    Cookie cookie = CookieDecoder.decode(it)
                    Cookie cookieToAdd=new Cookie(cookie.name,cookie.value,false,null,null,-1,false,false)
                    if (!(cookieToAdd in cookies)&&cookieToAdd.value.trim())
                        cookies.add(cookieToAdd)
                }
            }
        }
    }

    void setCookie(Cookie cookie) {
        boolean hasCookie = false
        for (existCookie in cookies) {
            if (existCookie.name == cookie.name) {
                if (!cookie.value.trim().empty) {
                    cookies.remove(existCookie)
                    cookies.add(cookie)
                }
                hasCookie = true
                break
            }
        }
        if (!hasCookie)
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
        def response = client.prepareGet(url).addHeader("USER-AGENET", USER_AGENET).addHeader("Referrer", referrer).addHeader("Accept", "*/*").setCookies(cookies).execute(a).get()
        syncCookies(response.headers.get("Set-Cookie"))
    }

    void clearCookies(){
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
            paramList.add(new Param(key, URLEncoder.encode(value,"UTF-8")))
        }
        return paramList
    }

    void close() {
        client.close()
    }

}
