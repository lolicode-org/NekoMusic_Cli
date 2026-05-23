<p align="center">
    <img src="./src/main/resources/assets/nekomusiccli/icon.png" alt="Icon" width=128>
</p>

# NekoMusic Client

适用于Minecraft Fabric 1.19+的联机音乐播放器

> [!CAUTION]
> 本模组已停止开发。如果有严重的 bug，你仍可在 issues 里提出，我可能会修复它们。但本模组将不会再接收到任何功能更新，也不会被移植到任何新的 Minecraft 版本。
>
> 这并不代表我们放弃了本模组的用户。相反，我们编写了一个全新的模组，它在包含本模组几乎全部功能的基础上，额外加入了对单人模式的支持、简洁完善的 GUI、由插件驱动的多音源支持，以及，仍然 100% 开源。请访问 [MoeMusic](https://modrinth.com/mod/moemusic) 来获取新的核心模组，访问 [MoeMusic NCM Lite Source](https://github.com/lolicode-org/MoeMusic-NCM-Lite-Source) 来获取某云音乐音源（非必需），以及访问 [MoeMusic Plasmo Voice Ducking](https://github.com/lolicode-org/moemusic-plasmo-voice-ducking) 来获取对 Plasmo Voice 语音聊天模组的集成（用于在有人说话时自动降低音乐音量）。
>
> 如果你是服务器管理员，我们推荐将你的服务器也升级到 MoeMusic。无需担心为玩家带来困扰：我们提供了 [MoeMusic NekoCompat](https://github.com/lolicode-org/MoeMusic-NekoCompat) 用于与 NekoMusic Client 保持兼容。

**下载：[Release](https://github.com/KoishiMoe/NekoMusic_Cli/releases)**

服务端：[NekoMusic_Server](https://github.com/lolicode-org/NekoMusic_Server)

> [!WARNING]  
> 本项目**是且仅是**一个适用于Minecraft **（国际JAVA版）** 的[模组](https://zh.wikipedia.org/wiki/index.php?curid=161167)，与任何同名或相似名称的项目与/或服务及其开发与/或运营方没有任何关联。也请不要在本项目下提出任何其他不相关项目的问题，谢谢。

----------------------------------

## 功能
* 支持mp3、flac、ogg(vorbis)格式
* 支持从音乐中途开始播放
* 歌词、歌曲信息、封面显示
* 完整的游戏内设置
* 可自定义的本地缓存

## FAQ
* 会支持1.18-吗
  * 目前版本不会，如果我有时间去做计划中的重构，可能会顺带支持旧版本。
* 会支持Forge吗
  * 已支持1.21.4以上版本的neoforge，请在release中查找对应条目
  * 原版forge目前暂不被支持
* 音量怎么调
  * 在Minecraft的声音设置中的`NekoMusic 音乐播放`类别中调整。
* 高级设置里都是什么
  * 域名白名单：只有白名单中的域名中的资源会被允许加载。该检查忽略端口号，且会匹配所有子域名。
  * 缓存大小：默认情况下，听过的音乐文件会被缓存下来，以节省后续加载的时间和流量。你可以根据自己的磁盘剩余空间进行调整，设为-1即无限制，设为0以禁用。参考：一首3分钟左右的320kbps的mp3文件（服务端默认配置）大约会占用10MB的空间。一首Flac大约会占用40MB左右。
  * 缓存路径：缓存存放的位置。 **注意这里不应该有任何你的私人文件或者其他程序的文件** 。如果你不知道这是什么，请不要修改。
    * 设计该选项主要是为了多系统或者将游戏装在可移动磁盘的用户，你可以使用相对路径（如`./cache/nekomusiccli`）来保证在多系统上的兼容性，或者使用环境变量（如`${APPDATA}/NekoMusicCli`）来避免将缓存存储在可移动磁盘上。

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
