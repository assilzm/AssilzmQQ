package utils

import org.apache.log4j.Logger

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

/**
 *
 * @author Assilzm
 * createTime 2014-5-19 10:13.
 */
public class JsUtils {

    /**
     * 记录日志类
     */
    private final static Logger logger = LogUtils.getLogger(JsUtils);



    static def execJs(String js)  {
        def a=null
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        try {
            a = engine.eval(js);
        } catch (ScriptException ex) {
            ex.printStackTrace();
        }
        return a
    }
}
