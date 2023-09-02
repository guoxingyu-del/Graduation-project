package test

import (
	"encoding/json"
	"fmt"
	"runtime/debug"
	"testing"
	"time"

	"github.com/JackTJC/gmFS_backend/pb_gen"
	"google.golang.org/protobuf/proto"
)

type testFormatType uint8

var (
	jsonType testFormatType = 1
	pbType   testFormatType = 2
)

var (
	n = &pb_gen.Node{
		NodeType:    pb_gen.NodeType_Dir,
		NodeName:    "xxxxxx",
		NodeContent: []byte("content"),
		CreateTime:  1,
		UpdateTime:  1,
		SubNodeList: nil,
		SecretKey:   []byte("key"),
	}
	xAxis = []int{1, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000}
)

// 序列化时间消耗对比
func TestPbJsonMarshal(t *testing.T) {
	debug.SetGCPercent(-1)
	t.Log("-----------------------------json marshal-----------------------------------")
	for _, x := range xAxis {
		jsonTypeMarshal(x)
	}
	t.Log("-----------------------------pb marshal-------------------------------------")
	for _, x := range xAxis {
		pbTypeMarshal(x)
	}
}

func TestJsonPbUnmarshal(t *testing.T) {
	debug.SetGCPercent(-1)
	jsonData, _ := json.Marshal(n)
	pbData, _ := proto.Marshal(n)
	t.Log("--------------------------------------json unmarshal----------------------")
	for _, x := range xAxis {
		jsonTypeUnmarshal(jsonData, x)
	}
	t.Log("-----------------------------------pb unmarshal-----------------------------------")
	for _, x := range xAxis {
		pbTypeUnmarshal(pbData, x)
	}
}

func jsonTypeMarshal(cnt int) {
	start := time.Now()
	for i := 0; i < cnt; i++ {
		json.Marshal(n)
	}
	end := time.Now()
	fmt.Printf("cnt:%5v, cost %10v \n", cnt, end.Sub(start))
}
func pbTypeMarshal(cnt int) {
	start := time.Now()
	for i := 0; i < cnt; i++ {
		proto.Marshal(n)
	}
	end := time.Now()
	fmt.Printf("cnt:%5v, cost %10v \n", cnt, end.Sub(start))
}

func jsonTypeUnmarshal(data []byte, cnt int) {
	recv := &pb_gen.Node{}
	start := time.Now()
	for i := 0; i < cnt; i++ {
		json.Unmarshal(data, recv)
	}
	end := time.Now()
	fmt.Printf("cnt:%5v, cost %10v \n", cnt, end.Sub(start))
}

func pbTypeUnmarshal(data []byte, cnt int) {
	recv := &pb_gen.Node{}
	start := time.Now()
	for i := 0; i < cnt; i++ {
		proto.Unmarshal(data, recv)
	}
	end := time.Now()
	fmt.Printf("cnt:%5v, cost %10v \n", cnt, end.Sub(start))
}
