package db

import (
	"context"
	"crypto/sha512"
	"encoding/base64"
	"time"

	"github.com/JackTJC/gmFS_backend/model"
	"github.com/JackTJC/gmFS_backend/pb_gen"
)

var SearchIndex *searchIdxDB

type searchIdxDB struct {
}

func (d *searchIdxDB) MCreate(ctx context.Context, indexs []*model.SearchIndex) error {
	for _, idx := range indexs {
		idx.CreateTime = time.Now()
		idx.UpdateTime = time.Now()
	}
	//fmt.Print(len(indexs), "-------------------------------------------------")
	conn := getDbConn(ctx)
	return conn.Create(indexs).Error
}

// 根据搜索令牌进行搜索
func (d *searchIdxDB) SearchByToken(ctx context.Context, searchToken *pb_gen.SearchToken) ([]string, error) {
	var ret []string
	//var index []*model.SearchIndex
	var index model.SearchIndex
	var err error
	var L = searchToken.GetL()
	var Jw = searchToken.GetJw()
	//mapLOffset := make(map[string]int)
	for {
		//offset, v := mapLOffset[L]
		//if !v {
		//	offset = 0
		//}
		index, err = singleSearch(ctx, L)
		if err != nil {
			return nil, err
		}
		//mapLOffset[L] = offset + 1
		ret = append(ret, index.CW)
		hash := SHA512(base64ToBytes(Jw), base64ToBytes(index.RW))
		old := xor(hash, base64ToBytes(index.IW))
		var flag = true
		for i := 0; i < 64; i++ {
			if old[i] != 0 {
				flag = false
				break
			}
		}
		if flag {
			break
		}
		var oldL, oldJw []byte
		for i := 0; i < 32; i++ {
			oldL = append(oldL, old[i])
		}
		for i := 32; i < 64; i++ {
			oldJw = append(oldJw, old[i])
		}
		L = bytesToBase64(oldL)
		Jw = bytesToBase64(oldJw)
	}
	return ret, nil
}

// 根据索引值L进行单次查找
func singleSearch(ctx context.Context, L string) (model.SearchIndex, error) {
	var index model.SearchIndex
	//var index []*model.SearchIndex
	conn := getDbConn(ctx)
	//fmt.Print(L, "-----------------------------------------------------")

	//conn.Model(&model.SearchIndex{}).Where("L=?", L).Order("create_time desc").Find(&indexs)
	//fmt.Print(len(indexs), "-----------------------------------------------------")
	//fmt.Print(indexs)
	if err := conn.Model(&model.SearchIndex{}).Where("L=?", L).Order("create_time desc").Find(&index).Error; err != nil {
		return index, err
	}
	return index, nil
}

// MulGetCIdByLAndJId 根据shareToken中的部分信息进行查找
func (d *searchIdxDB) MulGetCIdByLAndJId(ctx context.Context, L string, JId string) ([]string, error) {
	var CIds []string
	var index model.SearchIndex
	//var index []*model.SearchIndex
	var err error
	//mapLOffset := make(map[string]int)
	for {
		//offset, v := mapLOffset[L]
		//if !v {
		//	offset = 0
		//}

		index, err = singleSearch(ctx, L)
		if err != nil {
			return nil, err
		}
		//mapLOffset[L] = offset + 1
		CIds = append(CIds, index.CId)
		hash := SHA512(base64ToBytes(JId), base64ToBytes(index.RId))
		old := xor(hash, base64ToBytes(index.IId))
		var flag = true
		for i := 0; i < 64; i++ { // 检查L是否为空
			if old[i] != 0 {
				flag = false
				break
			}
		}
		if flag {
			break
		}
		var oldL, oldJId []byte
		for i := 0; i < 32; i++ {
			oldL = append(oldL, old[i])
		}
		for i := 32; i < 64; i++ {
			oldJId = append(oldJId, old[i])
		}
		L = bytesToBase64(oldL)
		JId = bytesToBase64(oldJId)
	}
	return CIds, nil
}
func base64ToBytes(s string) []byte {
	res, _ := base64.StdEncoding.DecodeString(s)
	return res
}

func bytesToBase64(bytes []byte) string {
	res := base64.StdEncoding.EncodeToString(bytes)
	return res
}

func SHA512(b1 []byte, b2 []byte) []byte {
	hash := sha512.Sum512(mergeBytes(b1, b2))
	var res []byte
	for i := 0; i < len(hash); i++ {
		res = append(res, hash[i])
	}
	return res
}

func xor(b1 []byte, b2 []byte) []byte {
	var res []byte
	var longByte, shortByte []byte
	if len(b1) >= len(b2) {
		longByte = b1
		shortByte = b2
	} else {
		longByte = b2
		shortByte = b1
	}
	for i := 0; i < len(shortByte); i++ {
		res = append(res, shortByte[i]^longByte[i])
	}
	for i := len(shortByte); i < len(longByte); i++ {
		res = append(res, longByte[i])
	}
	return res
}

func mergeBytes(b1 []byte, b2 []byte) []byte {
	if len(b1) == 0 || len(b2) == 0 {
		return nil
	}
	var b3 []byte
	for i := 0; i < len(b1); i++ {
		b3 = append(b3, b1[i])
	}
	for i := 0; i < len(b2); i++ {
		b3 = append(b3, b2[i])
	}
	return b3
}
