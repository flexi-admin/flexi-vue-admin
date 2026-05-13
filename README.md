这是一个完全由AI驱动的前后端一体的项目。可作为业务开发的基础，快速实现业务功能。

项目是以START.md为起点，由TRAE CN一点点实现出来的，虽然国产AI IDE和大模型有时不那么好用，需要更多时间成本来调整，但最终也能得到很好的效果。你可以查看START.md文件，了解项目的初始提示词。

AGENTS.md是留给AI IDE的，会自动附加到每次AI的对话中，防止AI遗忘一些关键的要求，也免得每次都要编写提醒AI的内容。

run-app.sh是开发模式下一键启动前后端项目的脚本，个人觉得自己手动管理应用的启动/重启比较好，而不是由AI去管理。对于Java项目，在IDEA里启动更佳。


几个关键的配置文件：
- backend/app/src/main/resources/application.yml
- frontend/vite.config.js
