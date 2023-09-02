package db

import (
	"context"
	"testing"

	"github.com/JackTJC/gmFS_backend/model"
)

func TestCreateUser(t *testing.T) {
	InitDB()
	err := User.Create(context.Background(), &model.UserInfo{
		UserName: "test",
		HashId:   "hashId",
		Email:    "email",
	})
	if err != nil {
		t.Error(err)
		t.FailNow()
	}
	t.Log("success")
}

func TestTrans(t *testing.T) {
	InitDB()
	ctx := context.Background()
	Transaction(ctx, func(ctx context.Context) error {
		User.Create(ctx, &model.UserInfo{
			UserName: "abcdefghi",
		})
		Node.Create(ctx, &model.Node{
			Name: "node name",
		})
		return nil
	})
}
