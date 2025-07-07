<template>

  <a-layout-header class="header">
    <div class="logo" >
      <router-link to="/welcome" style="color: white; font-size: 18px">
        ZSM 12306控台
      </router-link>
    </div>
    <div style="float: right; color: white">
      欢迎使用管理控台
    </div>
    <a-menu
        v-model:selectedKeys="selectedKeys1"
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px' }"
    >

      <a-menu-item key="/welcome">
        <router-link to="/welcome">
          <coffee-outlined /> &nbsp;欢迎
        </router-link>
      </a-menu-item>

      <a-menu-item key="/about">
        <router-link to="/about">
          <user-outlined /> &nbsp;关于
        </router-link>
      </a-menu-item>


    </a-menu>
  </a-layout-header>


</template>

<script>
import {computed, defineComponent, reactive, ref, watch} from 'vue';
import store from "@/store";
import router from "@/router";
import { useStore } from 'vuex'

export default defineComponent({
  name: "the-header-view",  // 此处加入可以解决Vue页面报错的问题
  setup() {

    const selectedKeys = ref([]);

    watch(() => router.currentRoute.value.path, (newValue) => {
      console.log('watch', newValue);
      selectedKeys.value = [];
      selectedKeys.value.push(newValue);
    }, {immediate: true});
    return {
      selectedKeys
    };
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.logo {
  float: left;
  height: 31px;
  width: 150px;
  color: white;
  font-size: 20px;
}
</style>