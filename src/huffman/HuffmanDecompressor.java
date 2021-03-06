package huffman;

import java.io.*;

public class HuffmanDecompressor {

	static Tree tree;
	static Tree.Node n;

	public static void decompress(String filePath) {

		try {

			String filename = filePath.substring(filePath.lastIndexOf('\\') + 1, filePath.lastIndexOf('.'));
			String outputFilePath = filePath.substring(0, filePath.lastIndexOf('\\') + 1) + filename + "d.txt";

			FileInputStream is = new FileInputStream(filePath);
			BufferedInputStream bis = new BufferedInputStream(is);

			BufferedInputStreamHelper helper = new BufferedInputStreamHelper(bis);

			ObjectInputStream ois;

			byte[] treeBytes;

			int treeSize = 0;
			int dataToRead;

			for (int i = 0; i < 4; i++) {
				dataToRead = helper.read();
				treeSize <<= 8;
				treeSize += (dataToRead & 0x000000FF);
			}

			treeBytes = new byte[treeSize];

			for (int i = 0; i < treeSize; i++) {
				dataToRead = helper.read();
				treeBytes[i] = (byte) (dataToRead & 0x000000FF);
			}

			ByteArrayInputStream bais = new ByteArrayInputStream(treeBytes);

			ois = new ObjectInputStream(bais);

			tree = (Tree) ois.readObject();
			ois.close();
			bais.close();

			byte bit;
			char characterToWrite = ' ';
			boolean ended = false;
			n = tree.getRoot();
			int charactersToRead = tree.getRoot().frequency;
			int charactersReaded = 0;
			PrintWriter pr = new PrintWriter(outputFilePath);
			StringBuilder sb = new StringBuilder();

			while (!ended && (dataToRead = helper.read()) != -1) {
				// System.out.printf("%d, %H \n", dataToRead & 0x000000FF, dataToRead &
				// 0x000000FF);
				for (int i = 7; i >= 0; i--) {
					bit = (byte) ((dataToRead >> i) & 0x01);
					if (searchCharacter(bit)) {
						charactersReaded++;
						characterToWrite = n.character;
						n = tree.getRoot();
						sb.append(characterToWrite);
					}
					if (charactersReaded == charactersToRead) {
						ended = true;
						break;
					}
				}
			}

			pr.write(sb.toString());

			pr.close();
			is.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static boolean searchCharacter(byte bit) {
		if (bit == 0) {
			n = n.left;
		} else {
			n = n.right;
		}

		return n.left == null;
	}

}
