package tech.grasshopper.reporter.component.text;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import tech.grasshopper.reporter.component.Component;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class MultipleTextComponent extends Component {

	private List<Text> texts;

	@Override
	public void display() {
		texts.forEach(t -> TextComponent.builder().content(content).text(t).build().display());
	}
}
