<template>
  <live2d
    :style="style"
    :model="[1, 53]"
    :direction="direction"
    :size="size"
    :tips="tips"
    :homePage="homePage"
  ></live2d>
</template>

<script>
/*
 * 项目中引用包时，将 import 内容替换
 * import live2d from 'vue-live2d'
 */
import live2d from "vue-live2d";
export default {
  name: "Live2d",
  components: {
    live2d,
  },
  data() {
    return {
      direction: "right",
      // style: '',
      style: "position: fixed; bottom: 0; right: 0; zIndex: 1",
      width: 500,
      height: 500,
      size: 350,
      homePage: "https://github.com/Raxcl",
      tips: {
        mouseover: [
          {
            selector: ".vue-live2d-tool .fa-comment",
            texts: ["猜猜我要说些什么？", "欣赏一些有意思的短句？"],
          },
          {
            selector: ".vue-live2d-tool .fa-user-circle",
            texts: ["想看看我的朋友们吗？"],
          },
          {
            selector: ".vue-live2d-tool .fa-street-view",
            texts: ["变装！"],
          },
          {
            selector: ".vue-live2d-tool .fa-camera-retro",
            texts: ["一起拍照留念？"],
          },
          {
            selector: ".vue-live2d-tool .fa-info-circle",
            texts: ["(＾Ｕ＾)ノ~ＹＯ"],
          },
          {
            selector: ".vue-live2d-tool .fa-times",
            texts: ["就要说再见了吗？"],
          },
        ],
        copy: [
          {
            selector: "document",
            texts: ["你都复制了些什么呀，转载要记得加上出处哦！"],
          },
        ],
        visibilitychange: [
          {
            selector: "document",
            texts: ["哇，你终于回来了～"],
          },
        ],
      },
    };
  },
  created() {
    // tips 未使用，可自行在组件绑定此参数，注意此参数只会初始化一次
    this.tips = this.tips;
  },
};
</script>

<style>
body {
  margin: 20px;
  padding: 0;
  /* position: fixed; */
  /* right: 40; */
  /* bottom: 40; */
}
</style>
