// Code generated by protoc-gen-go. DO NOT EDIT.
// versions:
// 	protoc-gen-go v1.26.0
// 	protoc        v3.20.3
// source: get_share_tokens.proto

package pb_gen

import (
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

type GetShareTokensRequest struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	UserId  string   `protobuf:"bytes,1,opt,name=userId,proto3" json:"userId,omitempty"`
	BaseReq *BaseReq `protobuf:"bytes,255,opt,name=baseReq,proto3" json:"baseReq,omitempty"`
}

func (x *GetShareTokensRequest) Reset() {
	*x = GetShareTokensRequest{}
	if protoimpl.UnsafeEnabled {
		mi := &file_get_share_tokens_proto_msgTypes[0]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *GetShareTokensRequest) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*GetShareTokensRequest) ProtoMessage() {}

func (x *GetShareTokensRequest) ProtoReflect() protoreflect.Message {
	mi := &file_get_share_tokens_proto_msgTypes[0]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use GetShareTokensRequest.ProtoReflect.Descriptor instead.
func (*GetShareTokensRequest) Descriptor() ([]byte, []int) {
	return file_get_share_tokens_proto_rawDescGZIP(), []int{0}
}

func (x *GetShareTokensRequest) GetUserId() string {
	if x != nil {
		return x.UserId
	}
	return ""
}

func (x *GetShareTokensRequest) GetBaseReq() *BaseReq {
	if x != nil {
		return x.BaseReq
	}
	return nil
}

type ShareMesssage struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	ShareToken   *ShareToken `protobuf:"bytes,1,opt,name=shareToken,proto3" json:"shareToken,omitempty"`
	SecretKey    string      `protobuf:"bytes,2,opt,name=secretKey,proto3" json:"secretKey,omitempty"`
	FileName     string      `protobuf:"bytes,3,opt,name=fileName,proto3" json:"fileName,omitempty"`
	ShareTokenId string      `protobuf:"bytes,4,opt,name=shareTokenId,proto3" json:"shareTokenId,omitempty"`
	IsShare      string      `protobuf:"bytes,5,opt,name=isShare,proto3" json:"isShare,omitempty"`
	CreateTime   int64       `protobuf:"varint,6,opt,name=createTime,proto3" json:"createTime,omitempty"`
}

func (x *ShareMesssage) Reset() {
	*x = ShareMesssage{}
	if protoimpl.UnsafeEnabled {
		mi := &file_get_share_tokens_proto_msgTypes[1]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *ShareMesssage) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*ShareMesssage) ProtoMessage() {}

func (x *ShareMesssage) ProtoReflect() protoreflect.Message {
	mi := &file_get_share_tokens_proto_msgTypes[1]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use ShareMesssage.ProtoReflect.Descriptor instead.
func (*ShareMesssage) Descriptor() ([]byte, []int) {
	return file_get_share_tokens_proto_rawDescGZIP(), []int{1}
}

func (x *ShareMesssage) GetShareToken() *ShareToken {
	if x != nil {
		return x.ShareToken
	}
	return nil
}

func (x *ShareMesssage) GetSecretKey() string {
	if x != nil {
		return x.SecretKey
	}
	return ""
}

func (x *ShareMesssage) GetFileName() string {
	if x != nil {
		return x.FileName
	}
	return ""
}

func (x *ShareMesssage) GetShareTokenId() string {
	if x != nil {
		return x.ShareTokenId
	}
	return ""
}

func (x *ShareMesssage) GetIsShare() string {
	if x != nil {
		return x.IsShare
	}
	return ""
}

func (x *ShareMesssage) GetCreateTime() int64 {
	if x != nil {
		return x.CreateTime
	}
	return 0
}

type GetShareTokensResponse struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	ShareMesssages []*ShareMesssage `protobuf:"bytes,1,rep,name=shareMesssages,proto3" json:"shareMesssages,omitempty"`
	BaseResp       *BaseResp        `protobuf:"bytes,255,opt,name=baseResp,proto3" json:"baseResp,omitempty"`
}

func (x *GetShareTokensResponse) Reset() {
	*x = GetShareTokensResponse{}
	if protoimpl.UnsafeEnabled {
		mi := &file_get_share_tokens_proto_msgTypes[2]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *GetShareTokensResponse) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*GetShareTokensResponse) ProtoMessage() {}

func (x *GetShareTokensResponse) ProtoReflect() protoreflect.Message {
	mi := &file_get_share_tokens_proto_msgTypes[2]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use GetShareTokensResponse.ProtoReflect.Descriptor instead.
func (*GetShareTokensResponse) Descriptor() ([]byte, []int) {
	return file_get_share_tokens_proto_rawDescGZIP(), []int{2}
}

func (x *GetShareTokensResponse) GetShareMesssages() []*ShareMesssage {
	if x != nil {
		return x.ShareMesssages
	}
	return nil
}

func (x *GetShareTokensResponse) GetBaseResp() *BaseResp {
	if x != nil {
		return x.BaseResp
	}
	return nil
}

var File_get_share_tokens_proto protoreflect.FileDescriptor

var file_get_share_tokens_proto_rawDesc = []byte{
	0x0a, 0x16, 0x67, 0x65, 0x74, 0x5f, 0x73, 0x68, 0x61, 0x72, 0x65, 0x5f, 0x74, 0x6f, 0x6b, 0x65,
	0x6e, 0x73, 0x2e, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x1a, 0x0c, 0x63, 0x6f, 0x6d, 0x6d, 0x6f, 0x6e,
	0x2e, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x22, 0x54, 0x0a, 0x15, 0x47, 0x65, 0x74, 0x53, 0x68, 0x61,
	0x72, 0x65, 0x54, 0x6f, 0x6b, 0x65, 0x6e, 0x73, 0x52, 0x65, 0x71, 0x75, 0x65, 0x73, 0x74, 0x12,
	0x16, 0x0a, 0x06, 0x75, 0x73, 0x65, 0x72, 0x49, 0x64, 0x18, 0x01, 0x20, 0x01, 0x28, 0x09, 0x52,
	0x06, 0x75, 0x73, 0x65, 0x72, 0x49, 0x64, 0x12, 0x23, 0x0a, 0x07, 0x62, 0x61, 0x73, 0x65, 0x52,
	0x65, 0x71, 0x18, 0xff, 0x01, 0x20, 0x01, 0x28, 0x0b, 0x32, 0x08, 0x2e, 0x42, 0x61, 0x73, 0x65,
	0x52, 0x65, 0x71, 0x52, 0x07, 0x62, 0x61, 0x73, 0x65, 0x52, 0x65, 0x71, 0x22, 0xd4, 0x01, 0x0a,
	0x0d, 0x73, 0x68, 0x61, 0x72, 0x65, 0x4d, 0x65, 0x73, 0x73, 0x73, 0x61, 0x67, 0x65, 0x12, 0x2b,
	0x0a, 0x0a, 0x73, 0x68, 0x61, 0x72, 0x65, 0x54, 0x6f, 0x6b, 0x65, 0x6e, 0x18, 0x01, 0x20, 0x01,
	0x28, 0x0b, 0x32, 0x0b, 0x2e, 0x53, 0x68, 0x61, 0x72, 0x65, 0x54, 0x6f, 0x6b, 0x65, 0x6e, 0x52,
	0x0a, 0x73, 0x68, 0x61, 0x72, 0x65, 0x54, 0x6f, 0x6b, 0x65, 0x6e, 0x12, 0x1c, 0x0a, 0x09, 0x73,
	0x65, 0x63, 0x72, 0x65, 0x74, 0x4b, 0x65, 0x79, 0x18, 0x02, 0x20, 0x01, 0x28, 0x09, 0x52, 0x09,
	0x73, 0x65, 0x63, 0x72, 0x65, 0x74, 0x4b, 0x65, 0x79, 0x12, 0x1a, 0x0a, 0x08, 0x66, 0x69, 0x6c,
	0x65, 0x4e, 0x61, 0x6d, 0x65, 0x18, 0x03, 0x20, 0x01, 0x28, 0x09, 0x52, 0x08, 0x66, 0x69, 0x6c,
	0x65, 0x4e, 0x61, 0x6d, 0x65, 0x12, 0x22, 0x0a, 0x0c, 0x73, 0x68, 0x61, 0x72, 0x65, 0x54, 0x6f,
	0x6b, 0x65, 0x6e, 0x49, 0x64, 0x18, 0x04, 0x20, 0x01, 0x28, 0x09, 0x52, 0x0c, 0x73, 0x68, 0x61,
	0x72, 0x65, 0x54, 0x6f, 0x6b, 0x65, 0x6e, 0x49, 0x64, 0x12, 0x18, 0x0a, 0x07, 0x69, 0x73, 0x53,
	0x68, 0x61, 0x72, 0x65, 0x18, 0x05, 0x20, 0x01, 0x28, 0x09, 0x52, 0x07, 0x69, 0x73, 0x53, 0x68,
	0x61, 0x72, 0x65, 0x12, 0x1e, 0x0a, 0x0a, 0x63, 0x72, 0x65, 0x61, 0x74, 0x65, 0x54, 0x69, 0x6d,
	0x65, 0x18, 0x06, 0x20, 0x01, 0x28, 0x03, 0x52, 0x0a, 0x63, 0x72, 0x65, 0x61, 0x74, 0x65, 0x54,
	0x69, 0x6d, 0x65, 0x22, 0x78, 0x0a, 0x16, 0x47, 0x65, 0x74, 0x53, 0x68, 0x61, 0x72, 0x65, 0x54,
	0x6f, 0x6b, 0x65, 0x6e, 0x73, 0x52, 0x65, 0x73, 0x70, 0x6f, 0x6e, 0x73, 0x65, 0x12, 0x36, 0x0a,
	0x0e, 0x73, 0x68, 0x61, 0x72, 0x65, 0x4d, 0x65, 0x73, 0x73, 0x73, 0x61, 0x67, 0x65, 0x73, 0x18,
	0x01, 0x20, 0x03, 0x28, 0x0b, 0x32, 0x0e, 0x2e, 0x73, 0x68, 0x61, 0x72, 0x65, 0x4d, 0x65, 0x73,
	0x73, 0x73, 0x61, 0x67, 0x65, 0x52, 0x0e, 0x73, 0x68, 0x61, 0x72, 0x65, 0x4d, 0x65, 0x73, 0x73,
	0x73, 0x61, 0x67, 0x65, 0x73, 0x12, 0x26, 0x0a, 0x08, 0x62, 0x61, 0x73, 0x65, 0x52, 0x65, 0x73,
	0x70, 0x18, 0xff, 0x01, 0x20, 0x01, 0x28, 0x0b, 0x32, 0x09, 0x2e, 0x42, 0x61, 0x73, 0x65, 0x52,
	0x65, 0x73, 0x70, 0x52, 0x08, 0x62, 0x61, 0x73, 0x65, 0x52, 0x65, 0x73, 0x70, 0x42, 0x35, 0x0a,
	0x19, 0x63, 0x6f, 0x6d, 0x2e, 0x67, 0x72, 0x61, 0x64, 0x75, 0x61, 0x74, 0x65, 0x2e, 0x64, 0x65,
	0x73, 0x69, 0x67, 0x6e, 0x2e, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x42, 0x0e, 0x47, 0x65, 0x74, 0x53,
	0x68, 0x61, 0x72, 0x65, 0x54, 0x6f, 0x6b, 0x65, 0x6e, 0x73, 0x5a, 0x08, 0x2e, 0x2f, 0x70, 0x62,
	0x5f, 0x67, 0x65, 0x6e, 0x62, 0x06, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x33,
}

var (
	file_get_share_tokens_proto_rawDescOnce sync.Once
	file_get_share_tokens_proto_rawDescData = file_get_share_tokens_proto_rawDesc
)

func file_get_share_tokens_proto_rawDescGZIP() []byte {
	file_get_share_tokens_proto_rawDescOnce.Do(func() {
		file_get_share_tokens_proto_rawDescData = protoimpl.X.CompressGZIP(file_get_share_tokens_proto_rawDescData)
	})
	return file_get_share_tokens_proto_rawDescData
}

var file_get_share_tokens_proto_msgTypes = make([]protoimpl.MessageInfo, 3)
var file_get_share_tokens_proto_goTypes = []interface{}{
	(*GetShareTokensRequest)(nil),  // 0: GetShareTokensRequest
	(*ShareMesssage)(nil),          // 1: shareMesssage
	(*GetShareTokensResponse)(nil), // 2: GetShareTokensResponse
	(*BaseReq)(nil),                // 3: BaseReq
	(*ShareToken)(nil),             // 4: ShareToken
	(*BaseResp)(nil),               // 5: BaseResp
}
var file_get_share_tokens_proto_depIdxs = []int32{
	3, // 0: GetShareTokensRequest.baseReq:type_name -> BaseReq
	4, // 1: shareMesssage.shareToken:type_name -> ShareToken
	1, // 2: GetShareTokensResponse.shareMesssages:type_name -> shareMesssage
	5, // 3: GetShareTokensResponse.baseResp:type_name -> BaseResp
	4, // [4:4] is the sub-list for method output_type
	4, // [4:4] is the sub-list for method input_type
	4, // [4:4] is the sub-list for extension type_name
	4, // [4:4] is the sub-list for extension extendee
	0, // [0:4] is the sub-list for field type_name
}

func init() { file_get_share_tokens_proto_init() }
func file_get_share_tokens_proto_init() {
	if File_get_share_tokens_proto != nil {
		return
	}
	file_common_proto_init()
	if !protoimpl.UnsafeEnabled {
		file_get_share_tokens_proto_msgTypes[0].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*GetShareTokensRequest); i {
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
		file_get_share_tokens_proto_msgTypes[1].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*ShareMesssage); i {
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
		file_get_share_tokens_proto_msgTypes[2].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*GetShareTokensResponse); i {
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
			RawDescriptor: file_get_share_tokens_proto_rawDesc,
			NumEnums:      0,
			NumMessages:   3,
			NumExtensions: 0,
			NumServices:   0,
		},
		GoTypes:           file_get_share_tokens_proto_goTypes,
		DependencyIndexes: file_get_share_tokens_proto_depIdxs,
		MessageInfos:      file_get_share_tokens_proto_msgTypes,
	}.Build()
	File_get_share_tokens_proto = out.File
	file_get_share_tokens_proto_rawDesc = nil
	file_get_share_tokens_proto_goTypes = nil
	file_get_share_tokens_proto_depIdxs = nil
}