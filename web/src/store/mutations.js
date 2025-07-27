/*
    直接操作state的地方
 */
export default {




    SET_SELECTED_DIR(state,value){
        // console.log("SET_SELECTED_DIR")
        state.selectedDir=value
    },


    SET_HEADER_TITLE(state,value){
        // console.log("SET_HEADER_TITLE")
        state.headerTitle=value
    },




    SET_SERVER_CONFIG(state,value){
        state.serverConfig=value
    },

    SET_BACKUP_STATUS(state,value){
        state.backupStatus=value
    }
}
