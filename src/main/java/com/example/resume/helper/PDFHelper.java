package com.example.resume.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PDFHelper {

	private static final Logger logger = LoggerFactory.getLogger(PDFHelper.class);

	private static PDPageContentStream contentStream;
	private static PDDocument document;

	private static float maxPageHeight = PDRectangle.A4.getHeight();
	private static float maxPageWidth = PDRectangle.A4.getWidth();

	private static float margin = 40f;

	private static float availablePageHeight = maxPageHeight - margin;
	private static float availablePageWidth = maxPageWidth - 2 * margin;
//	private static float allowedPageHeight = maxPageHeight - 2 * margin;

	private static float lineSpacing = 15f;

	private static PDFont headerFont = new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD);
	private static PDFont accentFont = new PDType1Font(Standard14Fonts.FontName.TIMES_ITALIC);
	private static PDFont boldAccentFont = new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD_ITALIC);
	private static PDFont generalFont = new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);

	private static int headerFontSize = 12;
	private static int accentFontSize = 8;
	private static int generalFontSize = 10;

	private static void addNewPage() throws Exception {
		PDPage page = null;
		try {
			contentStream.close();
			page = new PDPage(PDRectangle.A4);
			document.addPage(page);
			contentStream = new PDPageContentStream(document, page);
			availablePageHeight = maxPageHeight - margin;
		} catch (Exception $ex) {
			throw new Exception("Cannot add page to document. \n" + $ex);
		}
		logger.info("Page added to document.");
	}

	private static void initialise() throws Exception {
		PDPage page = null;
		try {
			page = new PDPage(PDRectangle.A4);
			document = new PDDocument();
			document.addPage(page);
			contentStream = new PDPageContentStream(document, page);
		} catch (Exception $ex) {
			throw new Exception("Cannot initialise document. \n" + $ex);
		}
		logger.info("Document initialised.");
	}

	private static void writeLine(String line, PDFont font, float fontSize) throws Exception {
		try {
			contentStream.beginText();
			contentStream.setFont(font, fontSize);
			contentStream.newLineAtOffset(margin, availablePageHeight);
			contentStream.showText(line);
			contentStream.endText();

			availablePageHeight -= lineSpacing;
		} catch (Exception $ex) {
			throw new Exception("Cannot write line. \n" + $ex);
		}
	}

	private static void createSection(String text, PDFont font, int fontSize) throws Exception {

		text.replaceAll("\r", "		");

//		float estimatedHeight = 0;

		float spaceWidth = (font.getSpaceWidth() / 1000) * fontSize;

		List<String> textLines = Arrays.asList(text.split("\n"));

		for (String line : textLines) {
			float wordWidth = 0f;
			List<String> lines = new ArrayList<String>();
			List<String> words = Arrays.asList(line.trim().split("\\s+"));
			StringBuffer currentLine = new StringBuffer();
			for (String word : words) {

				float actualWordWidth = (font.getStringWidth(word) / 1000f) * fontSize;

//				float potentialWidth = wordWidth + actualWordWidth + spaceWidth;
				float potentialWidth = wordWidth == 0 ? actualWordWidth : wordWidth + spaceWidth + actualWordWidth;

				if (potentialWidth > availablePageWidth) {
					logger.info("Available width exhausted.Info :: \nCurrent Sentence :: " + currentLine
							+ ", Current word ::  " + word + ", Current Word width :: " + wordWidth
							+ ", Available Page Width :: " + availablePageWidth + ", Space Width :: " + spaceWidth);
					lines.add(currentLine.toString());
					currentLine.setLength(0);
					wordWidth = 0f;
				}
				currentLine.append(word).append(" ");
				wordWidth += actualWordWidth + spaceWidth;
			}
			if (currentLine.length() > 0) {
				lines.add(currentLine.toString());
			}

//			estimatedHeight = lines.size() * (fontSize + lineSpacing);
//
//			int numPrintableLines = Math.round(estimatedHeight / (fontSize + lineSpacing));
//
//			if (estimatedHeight > allowedPageHeight) {
//				numPrintableLines = Math.round(allowedPageHeight / (fontSize + lineSpacing));
//			}
//
//			for (int i = 0; i < lines.size(); i++) {
//				if (i > numPrintableLines) {
//					addNewPage();
//					numPrintableLines = lines.size() - i;
//				}
//				writeLine(lines.get(i));
//			}
			for (String wrappedLine : lines) {

				if (availablePageHeight - (fontSize + lineSpacing) < margin) {
					addNewPage();
				}

				writeLine(wrappedLine, generalFont, generalFontSize);
			}
		}
	}

	private static void close(String path) throws Exception {
		try {
			contentStream.close();
			document.save(path);
			document.close();
		} catch (Exception $ex) {
			throw new Exception("Cannot close resources... \n" + $ex);
		}
		logger.info("Resources closed successfully...");
	}

	public static void createDocument(String path) {
		try {
			initialise();
			createSection(
					"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed blandit sed magna a ultrices. Suspendisse placerat tincidunt viverra. Duis commodo lacinia neque. Sed et molestie nibh, in vulputate dolor. Curabitur vulputate eu nisl mollis maximus. Sed non tellus quis tortor semper euismod sodales non nunc. Ut in orci scelerisque, fermentum magna vel, suscipit nulla. In ex neque, congue id egestas eu, blandit quis erat.\r\n"
							+ "\r\n"
							+ "Nunc et enim in ante dignissim fermentum id in lectus. Ut consectetur pulvinar ornare. Praesent quis felis ut ante blandit pretium. Curabitur porttitor finibus leo, quis elementum orci imperdiet et. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Ut feugiat elit quis lobortis tincidunt. Ut viverra sapien at nisi aliquam, eget eleifend nisi congue. Fusce elit elit, sodales et ornare id, suscipit sit amet libero.\r\n"
							+ "\r\n"
							+ "In mattis eu ligula vel tempor. Vestibulum erat nisi, malesuada non hendrerit ut, semper a arcu. Aliquam auctor tellus nec dignissim cursus. Maecenas vel auctor arcu. Pellentesque luctus nisi a ante blandit, ut tristique purus tincidunt. Praesent dapibus bibendum magna, a interdum dui. Ut quis placerat nisl. Donec quis est imperdiet, mattis orci vel, lobortis nibh.\r\n"
							+ "\r\n"
							+ "Nunc iaculis, mi vel finibus gravida, massa tellus varius felis, mollis vulputate odio augue vitae ex. Vestibulum sit amet enim ultrices, pharetra nibh a, sollicitudin felis. Sed accumsan gravida tempor. Mauris faucibus accumsan massa in ornare. Vestibulum ut sem vulputate nulla consequat finibus. Etiam sollicitudin nibh non nulla aliquet consectetur. Curabitur orci quam, sodales in ligula eget, vulputate tincidunt enim. Vivamus lacinia dolor ac sagittis mattis. Nunc sed felis at turpis bibendum vulputate a at eros. Suspendisse sed sapien porttitor, accumsan neque non, tempus nibh. Aliquam placerat massa augue, in dignissim massa cursus eget. Praesent iaculis turpis dui, eu elementum erat tincidunt at.\r\n"
							+ "\r\n"
							+ "Pellentesque id volutpat lorem. Aliquam consequat libero id tortor malesuada sagittis. Phasellus eget sagittis nisi. Pellentesque dapibus ultrices libero, sit amet auctor metus vehicula in. Aliquam erat volutpat. Suspendisse volutpat interdum odio a vehicula. Duis scelerisque tellus quis quam interdum faucibus. Duis nibh nunc, fringilla nec blandit eget, dignissim sed metus. In placerat purus purus, eu sagittis lorem tincidunt ut.\r\n"
							+ "\r\n"
							+ "Aenean malesuada urna enim, vulputate pulvinar metus ultricies id. Pellentesque suscipit nunc eget ligula aliquam, nec bibendum nulla dignissim. Duis vitae est id nisi lobortis venenatis ut eget justo. Etiam faucibus, felis quis tempus blandit, enim massa mattis dui, sit amet accumsan eros augue eu dui. In mattis dolor lobortis tempor blandit. Sed varius laoreet dictum. Donec et odio feugiat, scelerisque lectus non, mattis ante. Duis lobortis porta est, at varius purus facilisis imperdiet. Nullam eleifend ante eget tellus tempus dapibus. In faucibus risus mollis volutpat iaculis. In sagittis arcu id mauris ultricies venenatis. Sed tempus purus sit amet est posuere, a lobortis libero eleifend.\r\n"
							+ "\r\n"
							+ "Aliquam placerat fermentum metus. Maecenas gravida felis dolor, ac egestas ante tincidunt et. Etiam in sollicitudin metus. Fusce in erat quis justo viverra mollis. Duis eu purus laoreet, imperdiet tortor nec, rutrum mi. Nulla facilisi. Morbi elementum magna sollicitudin aliquam convallis. Fusce non rhoncus risus, vel condimentum ligula. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec tellus elit, egestas quis semper vitae, volutpat sed leo. Curabitur condimentum mauris vel lacus fermentum, quis ultrices tellus lobortis. Maecenas eu viverra libero. Donec non tortor consequat, interdum risus eu, mattis orci. Vivamus vitae egestas est, non semper leo.\r\n"
							+ "\r\n"
							+ "Vivamus ut lacus in purus porta rutrum. Aenean non velit varius, imperdiet velit id, bibendum urna. Nam at massa in augue finibus accumsan. Maecenas felis quam, eleifend vel pulvinar sed, bibendum eu eros. Donec sagittis ullamcorper metus vitae faucibus. Praesent efficitur cursus gravida. Sed nec lorem luctus, tincidunt velit et, ultricies sem. Duis tellus turpis, consequat vitae ligula vitae, aliquet interdum enim. Curabitur scelerisque mauris a orci tristique, id interdum magna laoreet. Praesent malesuada ligula sit amet turpis pellentesque semper. Fusce porttitor magna eu ornare tempus.\r\n"
							+ "\r\n"
							+ "Nulla scelerisque pretium nibh, sed pharetra sem congue non. Donec vitae maximus ante. Cras pharetra mi quis lobortis laoreet. Suspendisse quis cursus velit, ac tempor nisl. Morbi diam lacus, sodales a mauris et, iaculis feugiat ex. Aliquam volutpat massa quis mauris vulputate tincidunt. Integer vehicula felis et pulvinar dictum. Nunc tellus neque, feugiat sed enim sit amet, consequat tempus urna. Aliquam erat volutpat. Nulla consectetur eleifend felis vitae porta.\r\n"
							+ "\r\n"
							+ "Duis at velit sit amet odio accumsan rutrum. Aenean luctus hendrerit nulla a ornare. Donec placerat commodo elit sed suscipit. Aliquam erat volutpat. Nam cursus sem non odio accumsan dapibus. Praesent vitae euismod nunc, at tincidunt eros. Sed aliquam augue non nibh malesuada ullamcorper. Suspendisse in ante dignissim, convallis sem at, consequat urna.\r\n"
							+ "\r\n"
							+ "Quisque molestie, dui vitae luctus ultrices, est velit suscipit mi, quis egestas magna tortor ut mauris. Nam a dignissim magna. Vivamus eleifend vel lectus id posuere. Cras varius dui vel leo efficitur pharetra. Sed eu viverra quam. Praesent id feugiat dui. Ut id dolor et diam fringilla venenatis. Cras ornare ligula non quam sodales accumsan. Sed sollicitudin vehicula accumsan. Nullam venenatis erat maximus dolor sagittis aliquet. Vivamus egestas auctor efficitur. Nullam aliquet sit amet mauris ut dictum. Nullam tincidunt congue quam, eget bibendum augue congue nec. Donec faucibus id arcu vestibulum congue.\r\n"
							+ "\r\n"
							+ "Maecenas cursus odio non tellus faucibus, at porttitor dolor rutrum. Nunc luctus leo neque. Suspendisse bibendum nunc ac felis aliquam, a interdum turpis placerat. Nulla vel nunc tellus. Praesent bibendum elit vitae ullamcorper lobortis. Aliquam maximus efficitur mattis. Maecenas convallis accumsan feugiat. Etiam eget ipsum malesuada, dapibus leo placerat, pretium elit. Nulla eget quam at elit interdum sagittis at ultrices nisi. Praesent faucibus, metus id luctus sodales, ligula augue porta lacus, quis suscipit ligula purus vitae justo. Maecenas consequat faucibus gravida.\r\n"
							+ "\r\n"
							+ "Phasellus fringilla rhoncus vestibulum. Sed blandit metus eu urna tempor molestie. Phasellus ac justo quis leo pretium tincidunt ac id tellus. Phasellus nec lorem vel sapien venenatis scelerisque sed a lectus. Aenean vitae sagittis mi. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer dignissim nisl sem, blandit commodo elit ultricies vel. Praesent quam odio, iaculis quis quam vitae, rutrum semper ex. Mauris elementum est ac dolor elementum, eget efficitur ex dignissim. Etiam congue eu nisl a hendrerit. Ut sagittis ipsum a neque ultrices eleifend. Praesent justo nibh, ultrices a magna et, eleifend scelerisque ante. Morbi finibus luctus sollicitudin. Maecenas vitae accumsan velit. Donec rhoncus tristique nunc, nec maximus urna tincidunt vel.\r\n"
							+ "\r\n"
							+ "Nullam non leo rhoncus, lacinia mauris eu, porta leo. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Curabitur ultricies lorem nec purus euismod tempor. Suspendisse et pretium justo, at dignissim lacus. Etiam rhoncus est lacus, sit amet tincidunt libero luctus vitae. Suspendisse nec dictum mauris. Fusce iaculis in velit in vehicula.\r\n"
							+ "\r\n"
							+ "Maecenas tincidunt magna sed odio placerat, ac placerat lorem elementum. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus dictum maximus molestie. Phasellus id turpis a risus hendrerit rhoncus eu in lectus. Nulla a pellentesque dolor. Phasellus aliquam ac arcu volutpat dictum. Nunc id augue vitae diam vulputate feugiat. Morbi tristique a ipsum in egestas. Nullam vestibulum lobortis mauris. Nullam sodales turpis ac iaculis venenatis. Cras in nisi nulla. Cras pellentesque augue ut magna dictum, vel euismod tellus gravida. Etiam viverra urna a dapibus commodo.",
					generalFont, generalFontSize);
			close(path);
		} catch (Exception $ex) {
			logger.error("Error in create document :: ", $ex);
		}
		logger.info("Document created successfully...");
	}
}
