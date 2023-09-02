package objstore

import (
	"bytes"
	"context"
	"io/ioutil"

	"github.com/JackTJC/gmFS_backend/logs"
)

func UploadFile(ctx context.Context, key string, content []byte) error {
	reader := bytes.NewBuffer(content)
	_, err := client.Object.Put(ctx, key, reader, nil)
	if err != nil {
		logs.Sugar.Errorf("cos upload file error:%v", err)
		return err
	}
	return nil
}

func DownloadFile(ctx context.Context, key string) ([]byte, error) {
	resp, err := client.Object.Get(ctx, key, nil)
	if err != nil {
		logs.Sugar.Errorf("cos download file error:%v", err)
		return nil, err
	}
	content, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		logs.Sugar.Errorf("read all error:%v", err)
		return nil, err
	}
	return content, nil
}
