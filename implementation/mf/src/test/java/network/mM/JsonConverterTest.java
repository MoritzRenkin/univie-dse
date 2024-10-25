package network.mM;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class JsonConverterTest {

	@Test
	public void portTest_first() {
		JsonConvert jc = new JsonConvert(UUID.fromString("61297ae3-b7bf-474a-b839-7256d63b06c6"));
		NetworkConfig nc = jc.getConf();
		assertTrue(jc.getPort() == 9001);

	}

	@Test
	public void bridgingActivatedTest_first() {
		JsonConvert jc = new JsonConvert(UUID.fromString("61297ae3-b7bf-474a-b839-7256d63b06c6"));
		NetworkConfig nc = jc.getConf();
		assertTrue(jc.isBridgingActivated() == true);

	}

	@Test
	public void nodeTypeTest_frist() {
		JsonConvert jc = new JsonConvert(UUID.fromString("61297ae3-b7bf-474a-b839-7256d63b06c6"));
		NetworkConfig nc = jc.getConf();
		assertTrue(jc.getNodeType().equals("hub"));

	}

	@Test
	public void staticNeighboursCheck_frist_fist() {
		JsonConvert jc = new JsonConvert(UUID.fromString("61297ae3-b7bf-474a-b839-7256d63b06c6"));
		NetworkConfig nc = jc.getConf();
		assertTrue(nc.getStaticNeighbours().get(0).getPort()==9001);

	}
	@Test
	public void staticNeighboursCheck_frist_second() {
		JsonConvert jc = new JsonConvert(UUID.fromString("61297ae3-b7bf-474a-b839-7256d63b06c6"));
		NetworkConfig nc = jc.getConf();
		assertTrue(nc.getStaticNeighbours().get(1).getIp().toString().equals("/10.101.104.17"));

	}

	@Test
	public void portTest_second() {
		JsonConvert jc = new JsonConvert(UUID.fromString("fbe0539e-c16c-4472-b78b-30c9009726d3"));
		NetworkConfig nc = jc.getConf();
		assertTrue(nc.getPort() == 9002);

	}

	@Test
	public void bridgingActivatedTest_second() {
		JsonConvert jc = new JsonConvert(UUID.fromString("1edc91e8-eda8-4ccd-95e3-c1d9e6076c3f"));
		NetworkConfig nc = jc.getConf();
		assertTrue(jc.isBridgingActivated() == false);

	}
	@Test
	public void staticNeighboursCheck_second_fist() {
		JsonConvert jc = new JsonConvert(UUID.fromString("fbe0539e-c16c-4472-b78b-30c9009726d3"));
		NetworkConfig nc = jc.getConf();
		assertTrue(nc.getStaticNeighbours().get(0).getPort()==9001);

	}

	public void staticNeighboursCheck_size_first() {
		JsonConvert jc = new JsonConvert(UUID.fromString("2eec21d5-a756-4f79-b539-3f744166e917"));
		NetworkConfig nc = jc.getConf();
		assertTrue(nc.getStaticNeighbours().size()==1);

	}

	public void staticNeighboursCheck_size_second() {
		JsonConvert jc = new JsonConvert(UUID.fromString("fbe0539e-c16c-4472-b78b-30c9009726d3"));
		NetworkConfig nc = jc.getConf();
		assertTrue(nc.getStaticNeighbours().size()==8);

	}

}
