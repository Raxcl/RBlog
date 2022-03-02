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
        // { selector: ".fui-chat", "text": ["一言一语，一颦一笑。一字一句，一颗赛艇。"] },
        // { selector: ".fui-eye", "text": ["嗯··· 要切换 看板娘 吗？"] },
        // { selector: ".fui-user", "text": ["喜欢换装 Play 吗？"] },
        // { selector: ".fui-photo", "text": ["要拍张纪念照片吗？"] },
        // { selector: ".fui-info-circle", "text": ["这里有关于我的信息呢"] },
        // { selector: ".fui-cross", "text": ["你不喜欢我了吗..."] },
        // { selector: "#tor_show", "text": ["翻页比较麻烦吗，点击可以显示这篇文章的目录呢"] },
        // { selector: "#comment_go", "text": ["想要去评论些什么吗？"] },
        // { selector: "#night_mode", "text": ["深夜时要爱护眼睛呀"] },
        // { selector: "#qrcode", "text": ["手机扫一下就能继续看，很方便呢"] },
        // { selector: ".comment_reply", "text": ["要吐槽些什么呢"] },
        // { selector: "#back-to-top", "text": ["回到开始的地方吧"] },
        // { selector: "#author", "text": ["该怎么称呼你呢"] },
        // { selector: "#url", "text": ["你的家在哪里呢，好让我去参观参观"] },
        // { selector: "#textarea", "text": ["认真填写哦，垃圾评论是禁止事项"] },
        // { selector: ".OwO-logo", "text": ["要插入一个表情吗"] },
        // { selector: "#csubmit", "text": ["要[提交]^(Commit)了吗，首次评论需要审核，请耐心等待~"] },
        // { selector: ".ImageBox", "text": ["点击图片可以放大呢"] },
        // { selector: "input[name=s]", "text": ["找不到想看的内容？搜索看看吧"] },
        // { selector: ".previous", "text": ["去上一页看看吧"] },
        // { selector: ".next", "text": ["去下一页看看吧"] },
        // { selector: ".dropdown-toggle", "text": ["这里是菜单"] },
        // { selector: "c-player a.play-icon", "text": ["想要听点音乐吗"] },
        // { selector: "c-player div.time", "text": ["在这里可以调整<span style=\"color:#0099cc;\">播放进度</span>呢"] },
        // { selector: "c-player div.volume", "text": ["在这里可以调整<span style=\"color:#0099cc;\">音量</span>呢"] },
        // { selector: "c-player div.list-button", "text": ["<span style=\"color:#0099cc;\">播放列表</span>里都有什么呢"] },
        // { selector: "c-player div.lyric-button", "text": ["有<span style=\"color:#0099cc;\">歌词</span>的话就能跟着一起唱呢"] },
        // { selector: ".waifu #live2d", "text": ["干嘛呢你，快把手拿开", "鼠…鼠标放错地方了！"] }
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
        click: [
        {
            selector: ".vue-live2d-main",
            texts: [
                "是…是不小心碰到了吧",
                "萝莉控是什么呀",
                "你看到我的小熊了吗",
                "再摸的话我可要报警了！⌇●﹏●⌇",
                "110吗，这里有个变态一直在摸我(ó﹏ò｡)"
            ]
        }
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
