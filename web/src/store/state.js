
/*
存放各种需要共用的属性
 */
export default {



    selectedDir:[],//备份源
    headerTitle:null,//标题
    serverConfig:{
        addr:null,//服务器地址
        secret:null,//密钥
        checkMd5:null,//md5校验
    },
    backupStatus:0, //0 准备，1运行中，2暂停

}
