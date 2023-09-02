package test

import (
	"testing"

	"github.com/JackTJC/gmFS_backend/pb_gen"
	"github.com/golang/protobuf/jsonpb"
	"google.golang.org/protobuf/proto"
)

func TestProtoMarshal(t *testing.T) {
	node := &pb_gen.Node{
		NodeType: pb_gen.NodeType_Dir,
		NodeName: "123455",
	}
	b, err := proto.Marshal(node)
	if err != nil {
		t.Error(err)
		t.FailNow()
	}
	t.Log(string(b))
	m := jsonpb.Marshaler{}
	s, err := m.MarshalToString(node)
	if err != nil {
		t.Error(err)
		t.FailNow()
	}
	t.Log(s)
}
