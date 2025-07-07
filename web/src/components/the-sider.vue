<template>
  <a-layout-sider width="200" style="background: #fff">
    <a-menu
        v-model:selectedKeys="selectedKeys"
        v-model:openKeys="openKeys"
        mode="inline"
        :style="{ height: '100%', borderRight: 0 }"
    >

      <a-menu-item key="/welcome">
        <router-link to="/welcome">
          <coffee-outlined /> &nbsp;欢迎
        </router-link>
      </a-menu-item>

      <a-menu-item key="/passenger">
        <router-link to="/passenger">
          <user-outlined /> &nbsp;乘车人管理
        </router-link>
      </a-menu-item>

      <a-menu-item key="/ticket">
        <router-link to="/ticket">
          <user-outlined /> &nbsp;余票查询
        </router-link>
      </a-menu-item>

      <a-menu-item key="/my-ticket">
        <router-link to="/my-ticket">
          <user-outlined /> &nbsp;我的车票
        </router-link>
      </a-menu-item>


    </a-menu>
  </a-layout-sider>
</template>

<script>
import {defineComponent, ref, watch} from 'vue';
import router from "@/router";
import store from "@/store";

export default defineComponent({
  name: "the-sider-view",
  // 此处加入可以解决Vue页面报错的问题
  setup() {

    let member = store.state.member;
    const selectedKeys = ref([]);

    watch(() => router.currentRoute.value.path, (newValue) => {
      console.log('watch', newValue);
      selectedKeys.value = [];
      selectedKeys.value.push(newValue);
    }, {immediate: true});
    return {
      member,
      selectedKeys
    };
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
