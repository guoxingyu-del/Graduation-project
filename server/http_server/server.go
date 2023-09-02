package http_server

import (
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/unrolled/secure"
)

func Main(isHTTPS bool) {
	r := gin.Default()
	r.Use(corsHandler())
	r.Use(contentTypeBeforeHandler())
	r.Use(contentTypeAfterHandler())
	setRoute(r)
	if isHTTPS {
		r.Use(tlsHandler(8888))
		r.RunTLS(":8888", "./config/cert/domain.pem", "./config/cert/domain.key")
	} else {
		r.Run(":8888")
	}
}

func tlsHandler(port int) gin.HandlerFunc {
	return func(c *gin.Context) {
		secureMiddleware := secure.New(secure.Options{
			SSLRedirect: true,
			SSLHost:     ":" + strconv.Itoa(port),
		})
		err := secureMiddleware.Process(c.Writer, c.Request)

		// If there was an error, do not continue.
		if err != nil {
			c.Abort()
			return
		}

		c.Next()
	}
}

func corsHandler() gin.HandlerFunc {
	return func(context *gin.Context) {
		requestMethod := context.Request.Method
		context.Writer.Header().Set("Access-Control-Allow-Origin", "*")
		context.Header("Access-Control-Allow-Origin", "*") // 设置允许访问所有域
		context.Header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE,UPDATE")
		context.Header("Access-Control-Allow-Headers", "Authorization, Content-Length, X-CSRF-Token, Token,session,X_Requested_With,Accept, Origin, Host, Connection, Accept-Encoding, Accept-Language,DNT, X-CustomHeader, Keep-Alive, User-Agent, X-Requested-With, If-Modified-Since, Cache-Control, Content-Type, Pragma,token,openid,opentoken")
		context.Header("Access-Control-Expose-Headers", "Content-Length, Access-Control-Allow-Origin, Access-Control-Allow-Headers,Cache-Control,Content-Language,Content-Type,Expires,Last-Modified,Pragma,FooBar")
		context.Header("Access-Control-Max-Age", "172800")
		context.Header("Access-Control-Allow-Credentials", "false")
		if requestMethod == "OPTIONS" {
			context.JSON(http.StatusOK, `{"message":"success"}`)
		}
		//处理请求
		context.Next()
	}
}

func contentTypeBeforeHandler() gin.HandlerFunc {
	return func(c *gin.Context) {
		c.Keys = make(map[string]interface{})
		var format reqFormat
		if c.ContentType() == pbContentType {
			format = pbReqFormat
		} else {
			format = jsonReqFormat
		}
		c.Keys[formatKey] = format
		c.Next()
	}
}

func contentTypeAfterHandler() gin.HandlerFunc {
	return func(c *gin.Context) {
		format, ok := c.Keys[formatKey].(reqFormat)
		if !ok {
			c.Next()
			return
		}
		switch format {
		case jsonReqFormat:
			c.Header("Content-Type", jsonContentType)
		case pbReqFormat:
			c.Header("Content-Type", pbContentType)
		default:
			c.Header("Content-Type", jsonContentType)
		}
		c.Next()
	}
}
