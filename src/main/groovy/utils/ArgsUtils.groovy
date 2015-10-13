package utils

/**
 *
 * @author Administrator
 * @createTime ${Time}.
 */
class ArgsUtils {


    static Map<String, String> getUrlArgs(String argString){
        List<String> argList=argString.split("&")
        Map<String, String> argMaps=new HashMap<>()
        argList.each {
            List<String> temp=it.split("=")
            if(temp.size()>1)
                argMaps.put(temp.get(0),temp.get(1))
            else
                argMaps.put(temp.get(0),"")
        }
        argMaps
    }

    static List<String> getCommandArgs(String argString){
        return argString.split("\\s+")
    }
}
