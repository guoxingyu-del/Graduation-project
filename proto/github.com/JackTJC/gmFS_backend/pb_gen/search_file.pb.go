// Code generated by protoc-gen-go. DO NOT EDIT.
// versions:
// 	protoc-gen-go v1.26.0
// 	protoc        v3.19.2
// source: search_file.proto

package pb_gen

import (
	pb_gen "./pb_gen"
	protoreflect "google.golang.org/protobuf/reflect/protoreflect"
	protoimpl "google.golang.org/protobuf/runtime/protoimpl"
	reflect "reflect"
	sync "sync"
)

const (
	// Verify that this generated code is sufficiently up-to-date.
	_ = protoimpl.EnforceVersion(20 - protoimpl.MinVersion)
	// Verify that runtime/protoimpl is sufficiently up-to-date.
	_ = protoimpl.EnforceVersion(protoimpl.MaxVersion - 20)
)

// base64
type SearchFileRequest struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	NodeId  []int64         `protobuf:"varint,1,rep,packed,name=nodeId,proto3" json:"nodeId,omitempty"`
	BaseReq *pb_gen.BaseReq `protobuf:"bytes,255,opt,name=baseReq,proto3" json:"baseReq,omitempty"`
}

func (x *SearchFileRequest) Reset() {
	*x = SearchFileRequest{}
	if protoimpl.UnsafeEnabled {
		mi := &file_search_file_proto_msgTypes[0]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *SearchFileRequest) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*SearchFileRequest) ProtoMessage() {}

func (x *SearchFileRequest) ProtoReflect() protoreflect.Message {
	mi := &file_search_file_proto_msgTypes[0]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use SearchFileRequest.ProtoReflect.Descriptor instead.
func (*SearchFileRequest) Descriptor() ([]byte, []int) {
	return file_search_file_proto_rawDescGZIP(), []int{0}
}

func (x *SearchFileRequest) GetNodeId() []int64 {
	if x != nil {
		return x.NodeId
	}
	return nil
}

func (x *SearchFileRequest) GetBaseReq() *pb_gen.BaseReq {
	if x != nil {
		return x.BaseReq
	}
	return nil
}

type SearchFileResponse struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	NodeList []*pb_gen.Node   `protobuf:"bytes,1,rep,name=nodeList,proto3" json:"nodeList,omitempty"` // 搜索结果
	BaseResp *pb_gen.BaseResp `protobuf:"bytes,255,opt,name=baseResp,proto3" json:"baseResp,omitempty"`
}

func (x *SearchFileResponse) Reset() {
	*x = SearchFileResponse{}
	if protoimpl.UnsafeEnabled {
		mi := &file_search_file_proto_msgTypes[1]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *SearchFileResponse) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*SearchFileResponse) ProtoMessage() {}

func (x *SearchFileResponse) ProtoReflect() protoreflect.Message {
	mi := &file_search_file_proto_msgTypes[1]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use SearchFileResponse.ProtoReflect.Descriptor instead.
func (*SearchFileResponse) Descriptor() ([]byte, []int) {
	return file_search_file_proto_rawDescGZIP(), []int{1}
}

func (x *SearchFileResponse) GetNodeList() []*pb_gen.Node {
	if x != nil {
		return x.NodeList
	}
	return nil
}

func (x *SearchFileResponse) GetBaseResp() *pb_gen.BaseResp {
	if x != nil {
		return x.BaseResp
	}
	return nil
}

var File_search_file_proto protoreflect.FileDescriptor

var file_search_file_proto_rawDesc = []byte{
	0x0a, 0x11, 0x73, 0x65, 0x61, 0x72, 0x63, 0x68, 0x5f, 0x66, 0x69, 0x6c, 0x65, 0x2e, 0x70, 0x72,
	0x6f, 0x74, 0x6f, 0x1a, 0x0c, 0x63, 0x6f, 0x6d, 0x6d, 0x6f, 0x6e, 0x2e, 0x70, 0x72, 0x6f, 0x74,
	0x6f, 0x22, 0x50, 0x0a, 0x11, 0x53, 0x65, 0x61, 0x72, 0x63, 0x68, 0x46, 0x69, 0x6c, 0x65, 0x52,
	0x65, 0x71, 0x75, 0x65, 0x73, 0x74, 0x12, 0x16, 0x0a, 0x06, 0x6e, 0x6f, 0x64, 0x65, 0x49, 0x64,
	0x18, 0x01, 0x20, 0x03, 0x28, 0x03, 0x52, 0x06, 0x6e, 0x6f, 0x64, 0x65, 0x49, 0x64, 0x12, 0x23,
	0x0a, 0x07, 0x62, 0x61, 0x73, 0x65, 0x52, 0x65, 0x71, 0x18, 0xff, 0x01, 0x20, 0x01, 0x28, 0x0b,
	0x32, 0x08, 0x2e, 0x42, 0x61, 0x73, 0x65, 0x52, 0x65, 0x71, 0x52, 0x07, 0x62, 0x61, 0x73, 0x65,
	0x52, 0x65, 0x71, 0x22, 0x5f, 0x0a, 0x12, 0x53, 0x65, 0x61, 0x72, 0x63, 0x68, 0x46, 0x69, 0x6c,
	0x65, 0x52, 0x65, 0x73, 0x70, 0x6f, 0x6e, 0x73, 0x65, 0x12, 0x21, 0x0a, 0x08, 0x6e, 0x6f, 0x64,
	0x65, 0x4c, 0x69, 0x73, 0x74, 0x18, 0x01, 0x20, 0x03, 0x28, 0x0b, 0x32, 0x05, 0x2e, 0x4e, 0x6f,
	0x64, 0x65, 0x52, 0x08, 0x6e, 0x6f, 0x64, 0x65, 0x4c, 0x69, 0x73, 0x74, 0x12, 0x26, 0x0a, 0x08,
	0x62, 0x61, 0x73, 0x65, 0x52, 0x65, 0x73, 0x70, 0x18, 0xff, 0x01, 0x20, 0x01, 0x28, 0x0b, 0x32,
	0x09, 0x2e, 0x42, 0x61, 0x73, 0x65, 0x52, 0x65, 0x73, 0x70, 0x52, 0x08, 0x62, 0x61, 0x73, 0x65,
	0x52, 0x65, 0x73, 0x70, 0x42, 0x4f, 0x0a, 0x19, 0x63, 0x6f, 0x6d, 0x2e, 0x67, 0x72, 0x61, 0x64,
	0x75, 0x61, 0x74, 0x65, 0x2e, 0x64, 0x65, 0x73, 0x69, 0x67, 0x6e, 0x2e, 0x70, 0x72, 0x6f, 0x74,
	0x6f, 0x42, 0x0a, 0x53, 0x65, 0x61, 0x72, 0x63, 0x68, 0x46, 0x69, 0x6c, 0x65, 0x5a, 0x26, 0x67,
	0x69, 0x74, 0x68, 0x75, 0x62, 0x2e, 0x63, 0x6f, 0x6d, 0x2f, 0x4a, 0x61, 0x63, 0x6b, 0x54, 0x4a,
	0x43, 0x2f, 0x67, 0x6d, 0x46, 0x53, 0x5f, 0x62, 0x61, 0x63, 0x6b, 0x65, 0x6e, 0x64, 0x2f, 0x70,
	0x62, 0x5f, 0x67, 0x65, 0x6e, 0x62, 0x06, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x33,
}

var (
	file_search_file_proto_rawDescOnce sync.Once
	file_search_file_proto_rawDescData = file_search_file_proto_rawDesc
)

func file_search_file_proto_rawDescGZIP() []byte {
	file_search_file_proto_rawDescOnce.Do(func() {
		file_search_file_proto_rawDescData = protoimpl.X.CompressGZIP(file_search_file_proto_rawDescData)
	})
	return file_search_file_proto_rawDescData
}

var file_search_file_proto_msgTypes = make([]protoimpl.MessageInfo, 2)
var file_search_file_proto_goTypes = []interface{}{
	(*SearchFileRequest)(nil),  // 0: SearchFileRequest
	(*SearchFileResponse)(nil), // 1: SearchFileResponse
	(*pb_gen.BaseReq)(nil),     // 2: BaseReq
	(*pb_gen.Node)(nil),        // 3: Node
	(*pb_gen.BaseResp)(nil),    // 4: BaseResp
}
var file_search_file_proto_depIdxs = []int32{
	2, // 0: SearchFileRequest.baseReq:type_name -> BaseReq
	3, // 1: SearchFileResponse.nodeList:type_name -> Node
	4, // 2: SearchFileResponse.baseResp:type_name -> BaseResp
	3, // [3:3] is the sub-list for method output_type
	3, // [3:3] is the sub-list for method input_type
	3, // [3:3] is the sub-list for extension type_name
	3, // [3:3] is the sub-list for extension extendee
	0, // [0:3] is the sub-list for field type_name
}

func init() { file_search_file_proto_init() }
func file_search_file_proto_init() {
	if File_search_file_proto != nil {
		return
	}
	if !protoimpl.UnsafeEnabled {
		file_search_file_proto_msgTypes[0].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*SearchFileRequest); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_search_file_proto_msgTypes[1].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*SearchFileResponse); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
	}
	type x struct{}
	out := protoimpl.TypeBuilder{
		File: protoimpl.DescBuilder{
			GoPackagePath: reflect.TypeOf(x{}).PkgPath(),
			RawDescriptor: file_search_file_proto_rawDesc,
			NumEnums:      0,
			NumMessages:   2,
			NumExtensions: 0,
			NumServices:   0,
		},
		GoTypes:           file_search_file_proto_goTypes,
		DependencyIndexes: file_search_file_proto_depIdxs,
		MessageInfos:      file_search_file_proto_msgTypes,
	}.Build()
	File_search_file_proto = out.File
	file_search_file_proto_rawDesc = nil
	file_search_file_proto_goTypes = nil
	file_search_file_proto_depIdxs = nil
}