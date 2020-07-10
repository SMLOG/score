package com.example.score;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.score.common.Holder;
import com.example.score.common.Info;
import com.example.score.common.TechMan;
import com.example.score.common.TechTypeEnum;
import com.example.score.data.CacheMan;
import com.example.score.data.Item;

@SpringBootTest
public class ScoreApplicationTests {

	@Test
	void contextLoads() throws IOException {
		
		
		List<Item> itemList = TechMan.getInfoList();
		
		Holder[] index = TechMan.getTech("1.000001",TechTypeEnum.DAY);
		for(int i=0;i<itemList.size();i++) {
			
			Item item = itemList.get(i);
				//item.code="0.002565";
				//Item item2 = CacheMan.load(item.code);

				item.list = TechMan.getTech(item.code,TechTypeEnum.WEEK);
				
				//CacheMan.write2Cache(item);
				//System.out.println(i+"\t"+item.name+"\t"+item2.code+"\t"+item.turnover);	
			

		}
		

		
		
		
	}


}
