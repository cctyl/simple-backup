import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from "@/pages/Home.vue";
import Settings from "@/pages/setting/Settings.vue";
import History from "@/pages/History.vue";
import BackupSource from "@/pages/setting/BackupSource.vue";
import DirSelect from "@/pages/setting/DirSelect.vue";
import ServerConfig from "@/pages/setting/ServerConfig.vue";


Vue.use(VueRouter)

const router = new VueRouter({
    mode: 'hash',
    routes: [
        {
            path: '/',
            redirect: '/home',
            meta: { headerTitle: '首页' }
        },
        {
            path: '/home',
            component: Home,
            meta: { headerTitle: '首页' }
        },

        {
            path: '/settings',
            component: Settings,
            meta: { headerTitle: '设置' }

        },
        {
            path: '/settings/server-config',
            component: ServerConfig,
            meta: { headerTitle: '服务器设置',rightIcon:'qr_code_scanner' }

        },
        {
            path: '/settings/source',
            component: BackupSource,
            meta: { headerTitle: '备份源管理' }

        },
        {
            path: '/settings/source/select',
            component: DirSelect,
            meta: {
                headerTitle: '文件夹选择',
                icon:'keyboard_return',
                callBack: function () {
                    console.log("文件夹选择 callBack")
                   this.$router.back()
                },

                hideFooter:true

            }

        },
        {
            path: '/history',
            component: History,
            meta: { headerTitle: '备份历史' }
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