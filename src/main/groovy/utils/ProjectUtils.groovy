package utils

/**
 * 工程工具类
 * @author WeiWei
 * createTime 2014-9-11 16:41.
 */
class ProjectUtils {

    static String getProjectDir(){
        String projectPath = System.getProperty("user.dir").replaceAll(/(^.*?[\\\/][^\\\/]*[\\\/])src[\\\/]?.*/, "\$1")
        projectPath=projectPath.replaceAll(/\/?$/,"/")
        projectPath=projectPath.replaceAll(/[\\]/,"/")
        return projectPath
    }


    static String getProjectSrcDir(){
        return getProjectDir()+"src/"
    }

    static String getProjectMainDir(){
        return  getProjectSrcDir()+"main/"
    }

    static String getProjectResourcesDir(){
        return  getProjectMainDir()+"resources/"
    }

}
