package main

import (
	"fmt"
	"github.com/JackTJC/gmFS_backend/dal"
	"github.com/JackTJC/gmFS_backend/http_server"
	"github.com/JackTJC/gmFS_backend/logs"
	"github.com/JackTJC/gmFS_backend/util"
)

func main() {
	fmt.Println("init")
	logs.InitLog()
	dal.Init()
	//fmt.Println("end")
	util.Init()
	//fmt.Println("end")
	http_server.Main(true)
	fmt.Println("end")
	//res := util.GetHashCodeByStringHMAC("0933e54e76b24731a2d84b6b463ec04c", "1")
	//fmt.Printf("%s", hex.EncodeToString(res))
}
