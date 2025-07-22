import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from "@/pages/Home.vue";
import Settings from "@/pages/setting/Settings.vue";
import History from "@/pages/History.vue";
import BackupSource from "@/pages/setting/BackupSource.vue";
import DirSelect from "@/pages/setting/DirSelect.vue";


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

        {
            path: '/settings',
            component: Settings,

        },
        {
            path: '/settings/source',
            component: BackupSource,

        },
        {
            path: '/settings/source/select',
            component: DirSelect,

        },
        {
            path: '/history',
            component: History,
        },
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