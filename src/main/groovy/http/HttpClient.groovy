package http

import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.Param
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

    final static String USER_AGENET = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:41.0) Gecko/20100101 Firefox/41.0"

    static List<Cookie> cookies = new ArrayList<>()


    AsyncHttpClient client

    HttpClient() {
        client = new AsyncHttpClient()

    }

    String get(String url) {
        getBody(client.prepareGet(url))
    }

    String post(String url, Map<String, String> params) {
        getBody(client.preparePost(url), mapToParamList(params))
    }

    String post(String url, List<Param> params) {
        getBody(client.preparePost(url), params)
    }

    String getBody(AsyncHttpClient.BoundRequestBuilder request, List<Param> params = null) {
        def response = request.addHeader("USER_AGENET", USER_AGENET).addQueryParams(params).setCookies(cookies).execute().get()
        println "request:" + this.cookies
        println "response:" + response.cookies
        syncCookies(response.cookies)
        response.responseBody
    }

    static void syncCookies(List<Cookie> cookies) {
        this.cookies.clear()
        cookies.each {
            this.cookies.add(it)
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
        client.prepareGet(url).execute(a).get()
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
