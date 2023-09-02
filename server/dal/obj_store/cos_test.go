package objstore

import (
	"context"
	"testing"
)

func TestCOS(t *testing.T) {
	InitCOS()
	s, _, err := client.Service.Get(context.Background())
	if err != nil {
		t.Error(err)
		t.FailNow()
	}
	t.Log(client.BaseURL.BucketURL)
	for _, b := range s.Buckets {
		t.Log(b)
	}
}

func TestStore(t *testing.T) {
	InitCOS()
	ctx := context.Background()
	err := UploadFile(ctx, "test/123.bin", []byte("123"))
	if err != nil {
		t.Error(err)
		t.FailNow()
	}
}
