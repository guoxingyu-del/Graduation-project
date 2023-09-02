package config

import (
	"io/ioutil"
	"path"
	"runtime"
	"sync"

	"gopkg.in/validator.v2"
	"gopkg.in/yaml.v2"
)

var config *Config
var once sync.Once

type MySQL struct {
	Host   string `yaml:"Host"`
	Port   string `yaml:"Port"`
	User   string `yaml:"User"`
	Passwd string `yaml:"Passwd"`
}

type Redis struct {
	Host   string `yaml:"Host"`
	Port   string `yaml:"Port"`
	User   string `yaml:"User"`
	Passwd string `yaml:"Passwd"`
}

type COS struct {
	BucketURL  string `yaml:"BucketURL"`
	ServiceURL string `yaml:"ServiceURL"`
	SecretID   string `yaml:"SecretID"`
	SecretKey  string `yaml:"SecretKey"`
}

type Config struct {
	MySQL MySQL `yaml:"MySQL"`
	Reids Redis `yaml:"Redis"`
	COS   COS   `yaml:"COS"`
}

func GetInstance() *Config {
	once.Do(initConf)
	return config
}

func initConf() {
	content, err := ioutil.ReadFile("./config/config.yaml")
	if err != nil {
		_, b, _, _ := runtime.Caller(0)
		d, _ := path.Split(b)
		content, err = ioutil.ReadFile(d + "config.yaml")
		if err != nil {
			panic(err)
		}
	}
	config = &Config{}
	err = yaml.Unmarshal(content, config)
	if err != nil {
		panic(err)
	}
	if err := validator.Validate(config); err != nil {
		panic(err)
	}
}
