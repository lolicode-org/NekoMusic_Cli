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
* ogg(vorbis)格式需要完整下载到本地才能播放，所以会有一定的延迟
* 中途加入的玩家需要等当前曲目播放完毕才能听到音乐（目前客户端不支持seek，服务端没有记录播放时间，要支持的话可能需要改解码器以及服务端的播放逻辑）

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
* 缓存怎么没起作用
  * 一般来讲是因为缓存目录被其他进程占用了，这通常发生在你同时启动了多个游戏实例时。如果你确定你只启动了一份Minecraft实例，则可能是你的计算机上的其他程序正在使用该目录，你可以尝试关闭这些程序，或者修改缓存目录以避免冲突。
  * 由于请求库Okhttp3的限制，在多个Minecraft实例同时运行时共用缓存目录是不可行的，因此Mod对缓存目录进行了加锁。一般来说该锁会在程序退出时自动释放，但对锁的检测仅仅发生在游戏启动时，因此即使你退出了先前占用缓存目录的实例，你仍然需要重启其他实例才能继续使用缓存。
  * 如果启动时报缓存检测错误，一般是因为找不到缓存目录、没有权限读写等。此时会临时回落到默认的目录。请根据具体的报错信息检查你的缓存设置是否正确。
* 我发现你这长的和Coloryr的AllMusic好像，二者有什么关系
  * 本项目的前身是它的一个分支，但后来我对整个项目进行了重构，现在仅剩的关系为本客户端保留一部分原项目的基础功能
  * **因此，请不要将二者混用，也不要在二者的issue/交流群等地方提及对方的问题**

## 致谢
* [AllMusic](https://github.com/Coloryr/AllMusic_Client) 本项目的灵感来源
* [cloth-config](https://github.com/shedaniel/cloth-config) 简单易用的设置接口
* [badpackets](https://github.com/badasintended/badpackets) 优秀的服务端、客户端数据包收发解决方案
* [OkHttp](https://square.github.io/okhttp/) 强大的网络请求库
* [jlayer](http://www.javazoom.net/javalayer/javalayer.html) ([Github分支](https://github.com/umjammer/jlayer)) mp3解码
* [Flac-library-java](https://github.com/nayuki/FLAC-library-Java) Flac解码
* [Tika](https://tika.apache.org/) 强大的内容检测和分析框架

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
