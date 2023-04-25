"use strict";(self.webpackChunkdc_comic_documentation=self.webpackChunkdc_comic_documentation||[]).push([[760],{3905:(e,t,r)=>{r.d(t,{Zo:()=>u,kt:()=>d});var n=r(7294);function i(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function o(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function a(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?o(Object(r),!0).forEach((function(t){i(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):o(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function c(e,t){if(null==e)return{};var r,n,i=function(e,t){if(null==e)return{};var r,n,i={},o=Object.keys(e);for(n=0;n<o.length;n++)r=o[n],t.indexOf(r)>=0||(i[r]=e[r]);return i}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(n=0;n<o.length;n++)r=o[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(i[r]=e[r])}return i}var s=n.createContext({}),l=function(e){var t=n.useContext(s),r=t;return e&&(r="function"==typeof e?e(t):a(a({},t),e)),r},u=function(e){var t=l(e.components);return n.createElement(s.Provider,{value:t},e.children)},p="mdxType",b={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},m=n.forwardRef((function(e,t){var r=e.components,i=e.mdxType,o=e.originalType,s=e.parentName,u=c(e,["components","mdxType","originalType","parentName"]),p=l(r),m=i,d=p["".concat(s,".").concat(m)]||p[m]||b[m]||o;return r?n.createElement(d,a(a({ref:t},u),{},{components:r})):n.createElement(d,a({ref:t},u))}));function d(e,t){var r=arguments,i=t&&t.mdxType;if("string"==typeof e||i){var o=r.length,a=new Array(o);a[0]=m;var c={};for(var s in t)hasOwnProperty.call(t,s)&&(c[s]=t[s]);c.originalType=e,c[p]="string"==typeof e?e:i,a[1]=c;for(var l=2;l<o;l++)a[l]=r[l];return n.createElement.apply(null,a)}return n.createElement.apply(null,r)}m.displayName="MDXCreateElement"},2346:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>s,contentTitle:()=>a,default:()=>b,frontMatter:()=>o,metadata:()=>c,toc:()=>l});var n=r(7462),i=(r(7294),r(3905));const o={sidebar_position:7},a="Observability",c={unversionedId:"observability",id:"observability",title:"Observability",description:"Observability stack is composed of these primitives:",source:"@site/docs/07-observability.md",sourceDirName:".",slug:"/observability",permalink:"/dc-comics/docs/observability",draft:!1,editUrl:"https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/docs/07-observability.md",tags:[],version:"current",sidebarPosition:7,frontMatter:{sidebar_position:7},sidebar:"tutorialSidebar",previous:{title:"GraalVM (native-image)",permalink:"/dc-comics/docs/graalvm"},next:{title:"gRPC",permalink:"/dc-comics/docs/grpc"}},s={},l=[{value:"Logging",id:"logging",level:2}],u={toc:l},p="wrapper";function b(e){let{components:t,...r}=e;return(0,i.kt)(p,(0,n.Z)({},u,r,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("h1",{id:"observability"},"Observability"),(0,i.kt)("p",null,"Observability stack is composed of these primitives:"),(0,i.kt)("ul",null,(0,i.kt)("li",{parentName:"ul"},"Logging "),(0,i.kt)("li",{parentName:"ul"},"Healthcheck(s): this is the capacity to test through HTTP the server state, mainly used by Kubernetes to check if the application is ready (can get traffic) and if it is in a broken state (pod should be killed and restarted for example)."),(0,i.kt)("li",{parentName:"ul"},"Metrics: often coupled to opentelemetry, it enables to collect metrics (think time series) about your application. It can be technical (CPU usage for example) or business (number of queries, ...)."),(0,i.kt)("li",{parentName:"ul"},"Tracing: this is the ability to trace a business request (a ",(0,i.kt)("em",{parentName:"li"},"trace"),") end to end through all the system. Main collectors/UI are Jaegger. ")),(0,i.kt)("h2",{id:"logging"},"Logging"))}b.isMDXComponent=!0}}]);