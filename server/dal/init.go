package dal

import (
	"github.com/JackTJC/gmFS_backend/dal/cache"
	"github.com/JackTJC/gmFS_backend/dal/db"
	objstore "github.com/JackTJC/gmFS_backend/dal/obj_store"
)

func Init() {
	cache.InitCache()
	db.InitDB()
	objstore.InitCOS()
}
