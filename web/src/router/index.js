import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from "@/pages/Home.vue";

// 其他页面组件的导入
// import Review from "@/pages/Review.vue"


Vue.use(VueRouter)

const router = new VueRouter({
    mode: 'hash',
    routes: [
        {
            path: '/',
            redirect: '/home'
        },
        {
            path: '/home',
            component: Home,
        },
        // // 其他路由配置
        // {
        //     path: '/tasks',
        //     component: Tasks,
        // },
        // {
        //     path: '/whitelist',
        //     component: Whitelist,
        // },
        // {
        //     path: '/blacklist',
        //     component: Blacklist,
        // },
        // {
        //     path: '/settings',
        //     component: Settings,
        // },
        // {
        //     path: '/other-setting',
        //     component: OtherSetting,
        // },
        // {
        //     path: '/review',
        //     component: Review,
        // },
        // {
        //     path: '/history-video',
        //     component: HistoryVideo,
        // }, {
        //     path: '/watch-uploader-list',
        //     component: WatchUploaderList,
        // },

        //
        // {
        //     path: '/security',
        //     component: Security,
        // },
        // {
        //     path: '/logout',
        //     component: Logout,
        // },
    ]
})

router.beforeEach((to, from, next) => {
    // console.log("下一站：" + to.path)
    next()
})

export default router