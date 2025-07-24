

export default {

    /**
     * 设置已选择的文件夹对象
     * @param context
     * @param value
     */
    setSelectedDir(context, value) {


        //TODO 向Android发起请求，更新数据

        //commit
        context.commit('SET_SELECTED_DIR', value)
    },

    setAddr(context, value) {


        //TODO 向Android发起请求，更新数据

        //commit
        context.commit('SET_ADDR', value)
    },

    setSecret(context, value) {


        //TODO 向Android发起请求，更新数据

        //commit
        context.commit('SET_SECRET', value)
    },
}
