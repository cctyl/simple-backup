

export default {

    /**
     * 设置已选择的文件夹对象
     * @param context
     * @param value
     */
    setSelectedDir(context, value) {


        // 向Android发起请求，更新数据
        window.Android.setSelectDir(JSON.stringify(value));
        //commit
        context.commit('SET_SELECTED_DIR', value)
    },



    setServerConfig(context, value) {

        // 向Android发起请求，更新数据
        window.Android.setServerConfig(JSON.stringify( value));
        //commit
        context.commit('SET_SERVER_CONFIG', value)
    },
}
