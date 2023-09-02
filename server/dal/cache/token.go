package cache

import "fmt"

var Token *tokenCache

type tokenCache struct {
}

func (c *tokenCache) SetToken(token string, uid uint64) error {
	return redisClient.Set(token, fmt.Sprintf("%d", uid), 0).Err()
}

func (c *tokenCache) GetUID(token string) (uint64, error) {
	return redisClient.Get(token).Uint64()
}
