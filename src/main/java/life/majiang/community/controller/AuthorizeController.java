package life.majiang.community.controller;

import life.majiang.community.dto.AccessTokenDTO;
import life.majiang.community.dto.GithubUser;
import life.majiang.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;
    @GetMapping("/callback")
    public String callback(@RequestParam(value = "code")String code,
                           @RequestParam(value = "state")String state){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id("Iv1.7cd9a13d1b502876");
        accessTokenDTO.setClient_secret("ba32b120ecdeba57fef9359919c0b4b6cd4d38e3");
        accessTokenDTO.setCode(code);
        accessTokenDTO.setStata(state);
        accessTokenDTO.setRedirect_uri("http://localhost:8887/callback");
        //获取access_token
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        //获取用户信息
        GithubUser user = githubProvider.getUser(accessToken);
        System.out.println(user.getName());
        return "index";
    }
}
