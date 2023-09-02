# 项目结构
```
|-- Go
    |-- app.log
    |-- go.mod                              # 第三方模块信息
    |-- go.sum                              # 第三方模块信息
    |-- main.go                             # 入口程序
    |-- Makefile                            
    |-- config                              # 配置文件     
    |   |-- config.go                       # 配置信息
    |   |-- config.yaml                     # 具体配置信息
    |   |-- cert                            # 证书
    |       |-- cert.conf                   # 身份信息
    |       |-- domain.crt                  # 生成的证书
    |       |-- domain.key                  # 生成的证书
    |       |-- domain.p12       
    |       |-- domain.pem
    |       |-- Makefile                    # 证书生成指令
    |-- dal
    |   |-- init.go                         # 初始化
    |   |-- cache                           # Redis使用函数封装
    |   |   |-- init.go
    |   |   |-- token.go
    |   |-- db                              # MySQL使用函数封装
    |   |   |-- db_test.go
    |   |   |-- file_info.go
    |   |   |-- init.go
    |   |   |-- node.go
    |   |   |-- node_rel.go
    |   |   |-- search_index.go
    |   |   |-- share_file.go
    |   |   |-- share_file_token.go
    |   |   |-- user.go
    |   |-- obj_store                       # COS对象存储使用函数封装
    |       |-- cos_test.go
    |       |-- file.go
    |       |-- init.go
    |-- http_server                         # http通讯配置
    |   |-- body.go                         # 报文解析
    |   |-- route.go                        # http路由设置
    |   |-- server.go                       # 处理函数
    |-- logs                                # 日志配置
    |   |-- log.go                          
    |   |-- log_test.go
    |-- method                              # 不同路径的处理函数
    |   |-- change_password.go
    |   |-- create_dir.go
    |   |-- delete_file.go
    |   |-- delete_share_token.go
    |   |-- get_dir.go
    |   |-- get_node.go
    |   |-- get_node_id.go
    |   |-- ……
    |-- model                               # 数据实体
    |   |-- ddl.sql                         # 数据库建立语句
    |   |-- file_info.go
    |   |-- my.ini
    |   |-- node.go
    |   |-- node_rel.go
    |   |-- search_index.go
    |   |-- share_file.go
    |   |-- share_token.go
    |   |-- user.go
    |-- pb_gen                             # 通信接口文件
    |   |-- change_password.pb.go
    |   |-- common.pb.go
    |   |-- create_dir.pb.go
    |   |-- delete_file.pb.go
    |   |-- delete_share_token.pb.go
    |   |-- get_dir.pb.go
    |   |-- get_node.pb.go
    |   |-- get_node_id.pb.go
    |   |-- get_share_tokens.pb.go
    |   |-- ping.pb.go
    |   |-- search_file.pb.go
    |   |-- send_search_token.pb.go
    |   |-- ……
    |-- test                                # 函数测试文件
    |   |-- common_test.go
    |   |-- create_dir_test.go
    |   |-- get_node_test.go
    |   |-- init.go
    |   |-- pb_json_cmp_test.go
    |   |-- proto_msg_test.go
    |   |-- upload_file_test.go
    |-- util                                # 工具类
        |-- id_gen.go                       # id生成函数
        |-- init.go                         # 初始化
        |-- tool.go                         # 工具类
```