 #注意
 每一个token 交易记录只查询 50 条

递归深度查询为3层
例如A作为入口，那么查询A->c ,再查询 c->d, 在查询 d->e 



#  使用 0x219a5733d918c955f6237512e0878ea07a372ea8 执行数据初始化
http://localhost:8080/init?firstToken=0xf0963cba9e900db0045f241498001e2f17378266


#  访问 http://localhost:8080/test 查看图形