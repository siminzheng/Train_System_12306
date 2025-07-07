<template>
  <a-layout id="components-layout-demo-top-side-2">
    <the-header-view></the-header-view>
    <a-layout>
      <the-sider-view></the-sider-view>
      <a-layout style="padding: 0 24px 24px">
<!--        <a-breadcrumb style="margin: 16px 0">-->
<!--          <a-breadcrumb-item>Home</a-breadcrumb-item>-->
<!--          <a-breadcrumb-item>List</a-breadcrumb-item>-->
<!--          <a-breadcrumb-item>App</a-breadcrumb-item>-->
<!--        </a-breadcrumb>-->
        <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
        >
<!--          所有会员总数：{{count}}-->
          <router-view></router-view>
        </a-layout-content>
      </a-layout>
    </a-layout>
  </a-layout>
</template>
<script>
import { UserOutlined, LaptopOutlined, NotificationOutlined } from '@ant-design/icons-vue';
import { defineComponent, ref } from 'vue';
import TheHeaderView from "@/components/the-header.vue";
import TheSiderView from "@/components/the-sider.vue";
import axios from "axios";
import {notification} from "ant-design-vue";
import router from "@/router";
export default defineComponent({
  components: {
    TheSiderView,
    TheHeaderView,

  },
  setup() {

    return {

    };
  },
});
</script>
<style>

</style>