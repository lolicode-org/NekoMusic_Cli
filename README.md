![Logo](./src/main/resources/assets/nekomusiccli/icon.png)

# NekoMusic Client

适用于Minecraft Fabric 1.19+的联机音乐播放器

**下载：[Release](https://github.com/KoishiMoe/NekoMusic_Cli/releases)**

服务端：[NekoMusic_Server](https://github.com/lolicode-org/NekoMusic_Server)

----------------------------------

## 功能
* 支持mp3、flac、ogg(vorbis)格式
* 歌词、歌曲信息、封面显示
* 完整的游戏内设置
* 可自定义的本地缓存

## 已知问题
* 封面旋转模式下小概率抽风（方形噪点图旋转），没找出来具体原因，欢迎issue讨论
* ogg(vorbis)格式需要完整下载到本地才能播放，所以会有一定的延迟
* 高级设置都需要重启游戏才能生效，这是有意为之
* 目前没写对seek的支持，所以中途加入的玩家需要等当前曲目播放完毕
* **缓存仅支持单个实例访问，多个实例共用缓存目录时，后启动的实例的缓存功能会被强制禁用**

## FAQ
* 会支持1.18-吗
  * 不会
* 会支持Forge吗
  * 也许会，不过不建议抱太大希望。需要全平台/旧版本支持的话建议考虑AllMusic
* 高级设置里都是什么
  * AllMusic兼容模式：如果你想使用同一个副本来游玩多个服务器，并且不想安装AllMusic的客户端，可以开启这个，这将使你能够在使用AllMusic协议的服务器内正常收听歌曲,但不支持Hud等。其效果类似于使用旧版的NekoMusic_Server。
  * 缓存大小：默认情况下，听过的音乐文件会被缓存下来，以节省后续加载的时间和流量。你可以根据自己的磁盘剩余空间进行调整，设为-1即无限制，设为0以禁用。参考：一首3分钟左右的320kbps的mp3文件（服务端默认配置）大约会占用10MB的空间。一首Flac大约会占用40MB左右。
  * 响应大小限制：限制单个响应的最大大小，在本Mod当前的实现中，这意味着单首歌/单张图片的最大大小。默认值应当能够处理正常的场景，但如果你需要播放更长的歌曲，可以考虑调大。请注意这可能带来更大的资源开销。极端情况下，在开启AllMusic兼容模式后，服内成员可能可以利用自定义url功能来发送过大的ogg文件，严重时可能会导致客户端崩溃。
  * 缓存路径：缓存存放的位置。 **注意这里不应该有任何你的私人文件或者其他程序的文件** 。如果你不知道这是什么，请不要修改。
    * 设计该选项主要是为了多系统或者将游戏装在可移动磁盘的用户，你可以使用相对路径（如`./cache/nekomusiccli`）来保证在多系统上的兼容性，或者使用环境变量（如`${APPDATA}/NekoMusicCli`）来避免将缓存存储在可移动磁盘上。
* 音量怎么调
  * 目前音量与声音设置中的`唱片机和音符盒`类别绑定，后续如果相关的依赖适配了新版本，会考虑将其独立分类
* ~~我发现你这长的和Coloryr的AllMusic好像，是抄的吗~~
  * 这个项目的[服务端](https://github.com/lolicode-org/NekoMusic_Server)原本定位就是AllMusic的Fabric端移植，后来由于开发上的一些问题，就自己重新实现了服务端，此时使用的协议和AllMusic是相同的，并没有独立的客户端。后来上游服务端适配Forge和Fabric后，本项目与其脱钩，并重新实现了客户端，由于功能上主要以AllMusic为参考，所以Hud基本一致。不过本项目的操作逻辑和设计思路都与上游有所不同，并且在开发中也并没有直接使用上游的源码，仅使用了上游涉及到协议相关的部分以实现简单的兼容。如果你对之前基于上游源码的客户端有兴趣，可以访问[这里](https://github.com/lolicode-org/AllMusic_Cli)。
  * 省流：曾经类似于亲戚，现在只是长得像而已
  * **因此，请不要将二者混用，也不要在二者的issue/交流群等地方提及对方的问题**

## 致谢
* [AllMusic](https://github.com/Coloryr/AllMusic_Client) 本项目的灵感来源
* [cloth-config](https://github.com/shedaniel/cloth-config) 简单易用的设置接口
* [badpackets](https://github.com/badasintended/badpackets) 优秀的服务端、客户端数据包收发解决方案
* [OkHttp](https://square.github.io/okhttp/) 强大的网络请求库
* [jlayer](http://www.javazoom.net/javalayer/javalayer.html) ([Github分支](https://github.com/umjammer/jlayer)) mp3解码
* [Flac-library-java](https://github.com/nayuki/FLAC-library-Java) Flac解码
* [Tika](https://tika.apache.org/) 强大的文件格式识别库

## 许可证
```text
Copyright (c) 2023 KoishiMoe

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
```
