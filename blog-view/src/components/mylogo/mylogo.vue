<template>
  <div class="wrapper-no7">
    <div class="logo">LOGO</div>
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
            <circle class="body-frontcircle" cx="150.86" cy="83.31" r="45.5" />
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
      <div class="logoani">LOGO</div>
    </div>
    <div class="layout notvisible">
      <div class="layout__nav">
        <ul class="layout__nav-left">
          <li></li>
          <li></li>
          <li></li>
          <li></li>
        </ul>
        <ul class="layout__nav-right">
          <li></li>
          <li></li>
          <li></li>
          <li></li>
        </ul>
      </div>
      <div class="layout__title"></div>
      <div class="layout__content">
        <div></div>
        <div></div>
        <div></div>
        <div></div>
        <div></div>
        <div></div>
        <div></div>
        <div></div>
        <div></div>
        <div></div>
        <div></div>
        <div></div>
        <div></div>
        <div></div>
        <div></div>
        <div></div>
      </div>
    </div>
    <button class="restart" @click="initAni">restart animation</button>
  </div>
</template>
<script>
import {gsap} from 'gsap';
export default {

  name: "MyLogo",
  data() {},
  mounted() {
    this.initAni()
  },
  methods: {
    //start-animation
    initAni() {
      const cat = document.querySelector(".cat")
    const head = document.querySelector(".head")
    const eyes = document.querySelectorAll(".eyes circle")
    const eyel = document.querySelectorAll(".eyes-l")
    const eyer = document.querySelectorAll(".eyes-r")
    const ears = document.querySelectorAll(".ears")
    const earsl = document.querySelector(".ears-l")
    const earsr = document.querySelector(".ears-r")
    const tail = document.querySelector(".tail")
    const backlegs = document.querySelector(".backlegs")
    const frontlegs = document.querySelector(".frontlegs")
    const frontlegs1 = document.querySelector(".frontlegs1")
    const backcircle = document.querySelector(".body-backcircle")
    const frontcircle = document.querySelector(".body-frontcircle")
    const bodybetween = document.querySelector(".body-between")
    const logo = document.querySelector(".logoani")

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
    
    const logofix = document.querySelector(".logo")
    const resetbutton = document.querySelector(".restart")
    const ani = document.querySelector(".animation-wrapper")
    const layout = document.querySelector(".layout")
      logofix.classList.add("visible");
      resetbutton.classList.add("visible");
      ani.classList.add("notvisible");
      layout.classList.remove("notvisible");
    },
    logonotVisible() {
   
    const logofix = document.querySelector(".logo")
    const resetbutton = document.querySelector(".restart")
    const ani = document.querySelector(".animation-wrapper")
    const layout = document.querySelector(".layout")
      logofix.classList.remove("visible");
      resetbutton.classList.remove("visible");
      ani.classList.remove("notvisible");
      layout.classList.add("notvisible");
    },
    resetit() {
      const cat = document.querySelector(".cat")
    const head = document.querySelector(".head")
    const eyes = document.querySelectorAll(".eyes circle")
    const eyel = document.querySelectorAll(".eyes-l")
    const eyer = document.querySelectorAll(".eyes-r")
    const ears = document.querySelectorAll(".ears")
    const earsl = document.querySelector(".ears-l")
    const earsr = document.querySelector(".ears-r")
    const tail = document.querySelector(".tail")
    const backlegs = document.querySelector(".backlegs")
    const frontlegs = document.querySelector(".frontlegs")
    const backcircle = document.querySelector(".body-backcircle")
    const frontcircle = document.querySelector(".body-frontcircle")
    const bodybetween = document.querySelector(".body-between")
    const logo = document.querySelector(".logoani")
   
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
body {
  height: 100vh;
  font-family: "Delius Swash Caps", cursive;
  text-align: center;
  color: #004466;
  background: #e4e5e6;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.wrapper-no7 {
  display: flex;
  justify-content: center;
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
.wrapper-no7 .restart {
  display: none;
  font-size: 20px;
  position: absolute;
  top: 50%;
  background-color: white;
  border: #bbbbbb 1px solid;
  box-shadow: none;
  padding: 20px;
  border-radius: 10px;
}
.wrapper-no7 .restart:hover {
  background-color: #ddd;
}
.wrapper-no7 .restart.visible {
  display: block;
}
.wrapper-no7 .layout {
  display: block;
  opacity: 1;
  width: 80%;
  position: absolute;
  top: 40px;
  transition: opacity 1s;
}
.wrapper-no7 .layout.notvisible {
  display: none;
  opacity: 0;
}
.wrapper-no7 .layout li {
  height: 15px;
  background-color: #bbbbbb;
}
.wrapper-no7 .layout ul {
  display: flex;
  margin: 0;
  padding: 0;
}
.wrapper-no7 .layout li {
  width: 50px;
  margin-left: 20px;
  list-style: none;
  background-color: #bbbbbb;
}
.wrapper-no7 .layout .layout__nav {
  display: flex;
  justify-content: space-between;
}
.wrapper-no7 .layout .layout__nav-left,
.wrapper-no7 .layout .layout__nav-right {
  width: 40%;
}
.wrapper-no7 .layout .layout__nav-left li:first-child {
  margin-left: 0;
}
.wrapper-no7 .layout .layout__nav-right {
  flex-direction: row-reverse;
}
.wrapper-no7 .layout .layout__title {
  width: 80%;
  height: 40px;
  background-color: #bbbbbb;
  margin-top: 150px;
  margin-left: auto;
  margin-right: auto;
}
.wrapper-no7 .layout .layout__content {
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
}
.wrapper-no7 .layout .layout__content div {
  width: 80%;
  height: 20px;
  background-color: #bbbbbb;
  margin-top: 20px;
}
.wrapper-no7 .layout .layout__content div:first-child {
  margin-top: 100px;
}
@media screen and (max-width: 500px) {
  .wrapper-no7 .layout {
    width: 100%;
  }
  .wrapper-no7 .layout .layout__nav,
  .wrapper-no7 .layout .layout__nav-left,
  .wrapper-no7 .layout .layout__nav-right {
    display: none;
  }
  .wrapper-no7 .layout .layout__title {
    margin-top: 100px;
  }
  .wrapper-no7 .layout .layout__content div:first-child {
    margin-top: 40px;
  }
}
@media screen and (max-width: 768px) {
  .wrapper-no7 .layout {
    width: 100%;
  }
  .wrapper-no7 .layout .layout__title {
    margin-top: 100px;
  }
  .wrapper-no7 .layout .layout__content div:first-child {
    margin-top: 40px;
  }
  .wrapper-no7 .layout .layout__title {
    margin-top: 70px;
  }
  .wrapper-no7 .layout .layout__nav {
    margin-top: 20px;
  }
  .wrapper-no7 .layout .layout__nav-left,
  .wrapper-no7 .layout .layout__nav-right {
    width: 35%;
  }
  .wrapper-no7 .logo {
    font-size: 45px;
  }
}
</style>