<template>
  <header ref="header">
    <div class="view">
      <img
        ref="imgbg1"
        src="https://cdn.raxcl.cn/blog-resource/img/bg1.jpg"
        style="display: none"
      />
      <div
        class="bg1"
        style="
          background-image: url('https://cdn.raxcl.cn/blog-resource/img/bg1.jpg');
        "
      ></div>
      <div
        class="bg2"
        style="
          background-image: url('https://cdn.raxcl.cn/blog-resource/img/bg2.jpg');
        "
      ></div>
      <div
        class="bg3"
        style="
          background-image: url('https://cdn.raxcl.cn/blog-resource/img/bg3.jpg');
        "
        v-show="loaded"
      ></div>
    </div>
    <!-- logo特效 -->
    <div class="wrapper-no7">
      <div class="logo" @click="initAni">RBlog</div>
      <div class="animation-wrapper">
        <div class="cat-wrapper">
          <svg
            class="cat"
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 207.68 163.77"
            preserveAspectRatio="none"
          >
            <title>cat</title>
            <g class="body">
              <circle class="body-backcircle" cx="76.86" cy="83.31" r="45.5" />
              <circle
                class="body-frontcircle"
                cx="150.86"
                cy="83.31"
                r="45.5"
              />
              <rect
                class="body-between"
                x="74.65"
                y="37.89"
                width="83.04"
                height="90.61"
              />
            </g>
            <circle class="head" cx="161.76" cy="52.75" r="45.92" />
            <g class="eyes">
              <circle class="eyes-l" cx="153.51" cy="46.5" r="8.25" />
              <circle class="eyes-r" cx="185.76" cy="46.5" r="8.25" />
            </g>
            <g class="ears">
              <polygon
                class="ears-l"
                points="132.76 19 132.76 0 149.55 8.81 132.76 19"
              />
              <polygon
                class="ears-r"
                points="179.44 11.2 197 4.06 195.16 22.9 179.44 11.2"
              />
            </g>
            <rect
              class="tail"
              x="-11"
              y="32.87"
              width="107"
              height="20"
              rx="9.58"
              ry="9.58"
              transform="translate(41.19 -18.41) rotate(45)"
            />
            <g class="backlegs">
              <path
                class="backlegs1"
                d="M74,124.85a6,6,0,0,0-4.7-7.07l-4.41-.89a6,6,0,0,0-7.07,4.7l-6.31,31.35a8.25,8.25,0,1,0,15.64,5,5.94,5.94,0,0,0,.44-1.33Z"
                transform="translate(-1.58 -0.92)"
              />
              <path
                class="backlegs2"
                d="M88.22,125.86a6,6,0,0,0-4.38-7.27l-4.37-1.08a6,6,0,0,0-7.27,4.38l-7.69,31a8.25,8.25,0,1,0,15.41,5.72,5.94,5.94,0,0,0,.5-1.31Z"
                transform="translate(-1.58 -0.92)"
              />
            </g>
            <g class="frontlegs">
              <path
                class="frontlegs1"
                d="M162.89,120.91a6,6,0,0,0-7.65-3.68L151,118.72a6,6,0,0,0-3.68,7.65l10.57,30.18a8.25,8.25,0,1,0,16-3.65,5.94,5.94,0,0,0-.3-1.37Z"
                transform="translate(-1.58 -0.92)"
              />
              <path
                class="frontlegs2"
                d="M175.77,120.08a6,6,0,0,0-7.48-4l-4.31,1.3a6,6,0,0,0-4,7.48l9.22,30.62a8.25,8.25,0,1,0,16.17-2.94,5.94,5.94,0,0,0-.24-1.38Z"
                transform="translate(-1.58 -0.92)"
              />
            </g>
          </svg>
        </div>
        <div class="logoani">RBlog</div>
      </div>
    </div>

    <div class="wrapper">
      <i class="ali-iconfont icon-down" @click="scrollToMain"></i>
    </div>
    <div class="wave1"></div>
    <div class="wave2"></div>
  </header>
</template>

<script>
import { mapState } from "vuex";
import { gsap } from "gsap";
import "../../common/font/font.css";

export default {
  name: "Header",
  data() {
    return {
      loaded: false,
    };
  },
  computed: {
    ...mapState(["clientSize"]),
  },
  watch: {
    "clientSize.clientHeight"() {
      this.setHeaderHeight();
    },
  },
  mounted() {
    /**
     * 因为bg3.jpg比较小，通常会比bg1.jpg先加载，显示出来会有一瞬间bg1显示一半，bg3显示一半，为了解决这个问题，增加这个判断，让bg1加载完毕后再显示bg3
     * HTML中使用img标签的原因：我个人想用div作为图片的载体，而只有img标签有图片加载完毕的onload回调，所以用一个display: none的img人柱力来加载图片
     * 当img中的src加载完毕后，会把图片缓存到浏览器，后续在div中用background url的形式将直接从浏览器中取出图片，不会下载两次图片
     */
    this.$refs.imgbg1.onload = () => {
      this.loaded = true;
    };
    this.setHeaderHeight();
    let startingPoint;
    const header = this.$refs.header;
    header.addEventListener("mouseenter", (e) => {
      startingPoint = e.clientX;
    });
    header.addEventListener("mouseout", (e) => {
      header.classList.remove("moving");
      header.style.setProperty("--percentage", 0.5);
    });
    header.addEventListener("mousemove", (e) => {
      let percentage = (e.clientX - startingPoint) / window.outerWidth + 0.5;
      header.style.setProperty("--percentage", percentage);
      header.classList.add("moving");
    });
    this.initAni();
  },
  methods: {
    //根据可视窗口高度，动态改变首图大小
    setHeaderHeight() {
      this.$refs.header.style.height = this.clientSize.clientHeight + "px";
    },
    //平滑滚动至正文部分
    scrollToMain() {
      window.scrollTo({
        top: this.clientSize.clientHeight,
        behavior: "smooth",
      });
    },
    //start-animation
    initAni() {
      const cat = document.querySelector(".cat");
      const head = document.querySelector(".head");
      const eyes = document.querySelectorAll(".eyes circle");
      const eyel = document.querySelectorAll(".eyes-l");
      const eyer = document.querySelectorAll(".eyes-r");
      const ears = document.querySelectorAll(".ears");
      const earsl = document.querySelector(".ears-l");
      const earsr = document.querySelector(".ears-r");
      const tail = document.querySelector(".tail");
      const backlegs = document.querySelector(".backlegs");
      const frontlegs = document.querySelector(".frontlegs");
      const frontlegs1 = document.querySelector(".frontlegs1");
      const backcircle = document.querySelector(".body-backcircle");
      const frontcircle = document.querySelector(".body-frontcircle");
      const bodybetween = document.querySelector(".body-between");
      const logo = document.querySelector(".logoani");

      const tl = gsap.timeline({ delay: 0.5, onComplete: this.logoVisible });
      const tl_eye = gsap.timeline({ delay: 1.5, repeat: 3, repeatDelay: 1 });
      this.resetit();
      this.logonotVisible();
      tl.to([head, eyes, ears], 0.2, { y: 45, x: 30 })
        .addLabel("twink")
        .to(eyel, 0.1, { scaleY: 1, y: 45 }, "twink-=0.1")
        .to(eyel, 0.1, { scaleY: 0.1, y: 55 }, "twink")
        .to(eyel, 0.1, { scaleY: 1, y: 45 }, "twink +=0.1")
        .to(eyer, 0.1, { scaleY: 0.1, y: 55 }, "twink")
        .to(eyer, 0.1, { scaleY: 1, y: 45 }, "twink +=0.1")
        .to(earsl, 0.1, { y: 8, x: -5, rotation: -20 }, "twink +=0.1")
        .to(earsr, 0.1, { y: 16, x: -15, rotation: -60 }, "twink +=0.1")
        .set(frontlegs, { opacity: 1 }, "+=0.5")
        .to(frontlegs1, 0.1, { y: 35, x: 15, rotation: -60 })
        .to(logo, 0.1, { x: 5 })
        .to(frontlegs1, 0.1, { y: 35, x: 5, rotation: -60 })
        .to(frontlegs1, 0.1, { y: 35, x: 15, rotation: -60 })
        .to(logo, 0.3, { x: 10 })
        .to(frontlegs1, 0.1, { y: 35, x: -5, rotation: -60 })
        .to(frontlegs1, 0.1, { y: 35, x: 25, rotation: -60 }, "+=0.5")
        .to(logo, 0.1, { x: 12 })
        .to(frontlegs1, 0.1, { y: 35, x: 5, rotation: -60 })
        .to(frontlegs1, 0.1, { y: 35, x: 25, rotation: -60 })
        .to(logo, 0.3, { x: 17 })
        .to(frontlegs1, 0.1, { y: 35, x: -5, rotation: -60 })
        .to(frontlegs1, 0.1, { y: 35, x: 35, rotation: -60 })
        .to(logo, 0.1, { x: 20 })
        .to(frontlegs1, 0.1, { y: 35, x: -5, rotation: -60 })
        .to(frontlegs1, 0.1, { y: 30, x: 30, rotation: -60, scaleY: 1.2 })
        .to(logo, 0.5, { x: 30 })
        .to(logo, 0.1, { rotation: 10 })
        .to(frontlegs1, 0.1, { y: 35, x: -15, rotation: -60, scaleY: 1 })
        .addLabel("wiggle")
        .to([head, eyes, ears], 0.1, { y: 48 }, "wiggle")
        .to(earsl, 0.1, { y: 10, x: -5, rotation: -20 }, "wiggle")
        .to(earsr, 0.1, { y: 18, x: -15, rotation: -60 }, "wiggle")
        .to(backcircle, 0.1, { y: 30, x: 40 }, "wiggle =-0.2")
        .to(backcircle, 0.1, { y: 30, x: 37 }, "wiggle =-0.1")
        .to(backcircle, 0.1, { y: 35, x: 40 }, "wiggle")
        .to(backcircle, 0.1, { y: 30, x: 40 })
        .to(backcircle, 0.1, { y: 30, x: 37 })
        .to(backcircle, 0.1, { y: 35, x: 40 })
        .to(backcircle, 0.1, { y: 30, x: 40 })
        .to(backcircle, 0.1, { y: 30, x: 37 })
        .to(backcircle, 0.1, { y: 35, x: 40 })
        .addLabel("logowiggle")
        .to(
          frontlegs1,
          0.1,
          { y: 35, x: 30, rotation: -60, scaleY: 1.25 },
          "logowiggle-=0.1"
        )
        .to(logo, 0.1, { rotation: 60, x: 70 }, "logowiggle")
        .to(logo, 0.5, { y: 50 }, "logowiggle+=0.1")
        .to(logo, 0.1, { rotation: 120 }, "logowiggle+=0.1")
        .to(logo, 0.1, { rotation: 270 }, "logowiggle+=0.2")
        .to(logo, 0.5, { y: 550, x: 90 }, "logowiggle+=0.2")
        .to(logo, 0.5, { opacity: 0 }, "logowiggle")
        .to(frontlegs1, 0.1, { y: 35, x: -15, rotation: -60, scaleY: 1 })
        .addLabel("jump")
        .to([head, eyes, ears], 0.1, { y: 5 }, "jump")
        .to(frontcircle, 0.1, { y: 15, x: 5 }, "jump")
        .to(bodybetween, 0.1, { rotation: -25, x: 25, y: 38 }, "jump")
        .to(frontlegs1, 0.1, { y: 0, x: 0, rotation: 0 }, "jump")
        .to(tail, 0.1, { y: 115, x: 20, rotation: -10 }, "jump")
        .to(frontlegs, 0.1, { y: -20 }, "jump+=0.1")

        .to([head, eyes, ears, frontcircle], 0.1, { x: 75, y: 5 }, "jump+=0.2")
        .to(frontcircle, 0.1, { x: 55, y: 5 }, "jump+=0.2")
        .to(
          bodybetween,
          0.1,
          { scaleX: 1, x: 45, y: 25, rotation: -15 },
          "jump+=0.2"
        )
        .to(backcircle, 0.1, { x: 50, y: 25 }, "jump+=0.2")
        .to(backlegs, 0.1, { x: 70 }, "jump+=0.2")

        .to(frontlegs, 0.1, { x: 250, y: 5, rotation: -45 }, "jump+=0.3")
        .to(frontcircle, 0.1, { x: 250 }, "jump+=0.3")
        .to(backcircle, 0.1, { y: 0, x: 250 }, "jump+=0.3")
        .to(
          bodybetween,
          0.1,
          { y: 0, x: 255, scaleX: 1, rotation: 4 },
          "jump+=0.3"
        )
        .to([head, eyes, ears], 0.1, { x: 275 }, "jump+=0.3")
        .to(tail, 0.1, { y: 25, x: 230, rotation: 15 }, "jump+=0.3")
        .to(backlegs, 0.1, { rotation: 45, x: 250, y: -25 }, "jump+=0.3")

        .to(frontlegs, 0.1, { x: 340, y: 105, rotation: -15 }, "jump+=0.4")
        .to(frontcircle, 0.1, { x: 340, y: 105 }, "jump+=0.4")
        .to(backcircle, 0.1, { y: 60, x: 350 }, "jump+=0.4")
        .to(
          bodybetween,
          0.1,
          { y: 70, x: 380, scaleX: 1, rotation: 35 },
          "jump+=0.4"
        )
        .to([head, eyes, ears], 0.1, { x: 385, y: 125 }, "jump+=0.4")
        .to(tail, 0.1, { y: 50, x: 370, rotation: 35 }, "jump+=0.4")
        .to(backlegs, 0.1, { rotation: 95, x: 350, y: 5 }, "jump+=0.4")

        .to(frontlegs, 0.1, { x: 420, y: 205, rotation: -15 }, "jump+=0.5")
        .to(frontcircle, 0.1, { x: 420, y: 205 }, "jump+=0.5")
        .to(backcircle, 0.1, { y: 160, x: 430 }, "jump+=0.5")
        .to(
          bodybetween,
          0.1,
          { y: 170, x: 460, scaleX: 1, rotation: 35 },
          "jump+=0.5"
        )
        .to([head, eyes, ears], 0.1, { x: 465, y: 225 }, "jump+=0.5")
        .to(tail, 0.1, { y: 150, x: 450, rotation: 35 }, "jump+=0.5")
        .to(backlegs, 0.1, { rotation: 95, x: 430, y: 95 }, "jump+=0.5")

        .to(cat, 0.5, { opacity: 0 }, "jump+=0.3");
      //cat blinking
      tl_eye
        .addLabel("twinkall")
        .to(eyel, 0.1, { scaleY: 0.1, y: 55 }, "twinkall")
        .to(eyel, 0.1, { scaleY: 1, y: 45 }, "twinkall +=0.1")
        .to(eyer, 0.1, { scaleY: 0.1, y: 55 }, "twinkall")
        .to(eyer, 0.1, { scaleY: 1, y: 45 }, "twinkall +=0.1");
    },
    logoVisible() {
      const logofix = document.querySelector(".logo");
      const ani = document.querySelector(".animation-wrapper");
      logofix.classList.add("visible");
      ani.classList.add("notvisible");
    },
    logonotVisible() {
      const logofix = document.querySelector(".logo");
      const ani = document.querySelector(".animation-wrapper");
      logofix.classList.remove("visible");
      ani.classList.remove("notvisible");
    },
    resetit() {
      const cat = document.querySelector(".cat");
      const head = document.querySelector(".head");
      const eyes = document.querySelectorAll(".eyes circle");
      const eyel = document.querySelectorAll(".eyes-l");
      const eyer = document.querySelectorAll(".eyes-r");
      const ears = document.querySelectorAll(".ears");
      const earsl = document.querySelector(".ears-l");
      const earsr = document.querySelector(".ears-r");
      const tail = document.querySelector(".tail");
      const backlegs = document.querySelector(".backlegs");
      const frontlegs = document.querySelector(".frontlegs");
      const backcircle = document.querySelector(".body-backcircle");
      const frontcircle = document.querySelector(".body-frontcircle");
      const bodybetween = document.querySelector(".body-between");
      const logo = document.querySelector(".logoani");

      gsap.set([head, eyes, ears], { y: 20, x: 30 });
      gsap.set(backcircle, { y: 35, x: 40 });
      gsap.set(cat, { opacity: 1 });
      gsap.set(bodybetween, { scaleX: 0.5, y: 35, x: 45, rotation: 0 });
      gsap.set(frontcircle, { y: 35, x: 10 });
      gsap.set(head, { y: 20 });
      gsap.set(eyel, { scaleY: 1 });
      gsap.set(eyer, { scaleY: 1 });
      gsap.set(ears, { y: 20 });
      gsap.set(tail, { y: 110, x: 30, rotation: 0 });
      gsap.set(backlegs, { rotation: -100, y: 55, x: 50 });
      gsap.set(frontlegs, { y: 0, x: 0, rotation: 0 });
      gsap.set(logo, { opacity: 1, x: 0, y: 0, rotation: 0 });
      gsap.set(earsl, { x: 0, y: 0, rotation: 0 });
      gsap.set(earsr, { x: 0, y: 0, rotation: 0 });
    },
  },
};
</script>

<style scoped>
header {
  --percentage: 0.5;
  user-select: none;
}

.view {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  display: flex;
  justify-content: center;
  transform: translatex(calc(var(--percentage) * 100px));
}

.view div {
  background-position: center center;
  background-size: cover;
  position: absolute;
  width: 110%;
  height: 100%;
}

.view .bg1 {
  z-index: 10;
  opacity: calc(1 - (var(--percentage) - 0.5) / 0.5);
}

.view .bg2 {
  z-index: 20;
  opacity: calc(1 - (var(--percentage) - 0.25) / 0.25);
}

.view .bg3 {
  left: -10%;
}

header .view,
header .bg1,
header .bg2 {
  transition: 0.2s all ease-in;
}

header.moving .view,
header.moving .bg1,
header.moving .bg2 {
  transition: none;
}

.wrapper {
  position: absolute;
  width: 100px;
  bottom: 150px;
  left: 0;
  right: 0;
  margin: auto;
  font-size: 26px;
  z-index: 100;
}

.wrapper i {
  font-size: 60px;
  opacity: 0.5;
  cursor: pointer;
  position: absolute;
  top: 55px;
  left: 20px;
  animation: opener 0.5s ease-in-out alternate infinite;
  transition: opacity 0.2s ease-in-out, transform 0.5s ease-in-out 0.2s;
}

.wrapper i:hover {
  opacity: 1;
}

@keyframes opener {
  100% {
    top: 65px;
  }
}

.wave1,
.wave2 {
  position: absolute;
  bottom: 0;
  transition-duration: 0.4s, 0.4s;
  z-index: 80;
}

.wave1 {
  background: url("https://cdn.raxcl.cn/blog-resource/img/wave1.png")
    repeat-x;
  height: 75px;
  width: 100%;
}

.wave2 {
  background: url("https://cdn.raxcl.cn/blog-resource/img/wave2.png")
    repeat-x;
  height: 90px;
  width: calc(100% + 100px);
  left: -100px;
}
/* body {
  height: 100vh;
  font-family: "Delius Swash Caps", cursive;
  text-align: center;
  color: #004466;
  background: #e4e5e6;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
} */

.wrapper-no7 {
  position: absolute;
  top: 20%;
  left: 20%;
  width: 220px;
  display: flex;
  justify-content: center;
  /* height: 100vh; */
  /* font-family: "Delius Swash Caps", cursive; */
  text-align: center;
  color: #004466;
  /* background: #e4e5e6; */
  /* display: flex; */
  align-items: center;
  /* justify-content: center; */
  /* overflow: hidden; */
}
.wrapper-no7 .animation-wrapper {
  display: flex;
}
.wrapper-no7 .animation-wrapper.notvisible {
  display: none;
}
.wrapper-no7 .cat {
  width: 80%;
  overflow: visible;
}
.wrapper-no7 .cat-wrapper {
  width: 300px;
}
.wrapper-no7 .eyes circle {
  fill: #fff;
}
.wrapper-no7 .frontlegs,
.wrapper-no7 .backlegs {
  opacity: 1;
}
.wrapper-no7 .logoani {
  align-self: flex-end;
  margin-left: -25px;
  font-size: 45px;
}
.wrapper-no7 .logo {
  font-size: 55px;
  opacity: 0;
  position: absolute;
  top: -40px;
}
.wrapper-no7 .logo.visible {
  opacity: 1;
  top: 40px;
  transition: all 1s;
}

@media screen and (max-width: 768px) {
  .wrapper-no7 .logo {
    font-size: 45px;
  }
}
</style>