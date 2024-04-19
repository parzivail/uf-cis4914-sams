package com.nitcoders.view;

import com.nitcoders.IconFont;
import com.nitcoders.MainWindow;
import com.nitcoders.model.Project;
import com.nitcoders.model.Subject;
import com.nitcoders.util.DialogUtil;
import com.nitcoders.util.ListUtil;
import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImInt;
import imgui.type.ImString;

import java.util.Arrays;

public class SubjectsEditor
{
	private static final Subject.Gender[] genders = Subject.Gender.values();
	private static final String[] genderNames = Arrays.stream(Subject.Gender.values()).map(Subject.Gender::getName).toArray(String[]::new);

	private static final ImInt nextSubjectAge = new ImInt(25);
	private static final ImInt nextSubjectGender = new ImInt(0);
	private static final ImString nextSubjectId = new ImString(512);

	private static Subject currentlyEditingSubject = null;

	public static void draw(Project project)
	{
		var innerSize = ImGui.getContentRegionAvail();
		if (ImGui.beginTable("subjectsTable", 2, ImGuiTableFlags.Resizable | ImGuiTableFlags.NoHostExtendY, innerSize.x, innerSize.y))
		{
			ImGui.tableSetupColumn("label", ImGuiTableColumnFlags.WidthFixed, 600);
			ImGui.tableSetupColumn("field", ImGuiTableColumnFlags.WidthStretch);

			ImGui.tableNextColumn();

			var frameSize = ImGui.getFrameHeight();

			var subjects = project.getSubjects();

			var idExists = subjects.stream().anyMatch(subject -> subject.getId().equals(nextSubjectId.get()));

			var columnWidth = ImGui.getContentRegionAvailX();

			ImGui.setNextItemWidth(columnWidth * 0.3f);
			ImGui.inputTextWithHint("##subjectId", "ID", nextSubjectId);
			ImGui.sameLine();

			ImGui.setNextItemWidth(columnWidth * 0.2f);
			ImGui.inputInt("##subjectAge", nextSubjectAge);
			ImGui.sameLine();

			ImGui.setNextItemWidth(columnWidth * 0.25f);
			ImGui.combo("##subjectGender", nextSubjectGender, genderNames);
			ImGui.sameLine();

			ImGui.setNextItemWidth(columnWidth * 0.25f);
			ImGui.beginDisabled(idExists || nextSubjectId.isEmpty());
			if (ImGui.button("Add Subject", -1, frameSize))
			{
				subjects.add(0, currentlyEditingSubject = new Subject(nextSubjectId.get(), nextSubjectAge.get(), genders[nextSubjectGender.get()]));

				try
				{
					// Auto-increment the ID if it's numeric
					var id = Integer.parseInt(nextSubjectId.get());
					id++;
					nextSubjectId.set(String.valueOf(id));
				}
				catch (Exception e)
				{
					// If the ID isn't numeric, just clear the field
					nextSubjectId.clear();
				}

				project.invalidateSubjectMap();
			}
			ImGui.endDisabled();

			if (idExists)
				ImGui.textColored(0xFF0000FF, "A subject with this ID already exists");

			if (ImGui.beginListBox("##subjectsList", -1, -1))
			{
				if (subjects.isEmpty())
					ImGui.textDisabled("No subjects yet, add one above.");
				else if (ImGui.beginTable("subjectsList", 2, ImGuiTableFlags.BordersH | ImGuiTableFlags.PadOuterX))
				{
					ImGui.tableSetupColumn("body[]", ImGuiTableColumnFlags.WidthStretch);
					ImGui.tableSetupColumn("actions[]", ImGuiTableColumnFlags.WidthFixed, -1);

					ListUtil.iterate(subjects, (iterator, i, subject) ->
					{
						ImGui.tableNextColumn();
						ImGui.spacing();

						ImGui.textWrapped(subject.getId());

						ImGui.pushFont(MainWindow.getSmallFont());
						ImGui.indent();
						ImGui.text("%sy %s".formatted(subject.getAge(), subject.getGender().getName()));

						ImGui.unindent();
						ImGui.popFont();

						ImGui.spacing();

						ImGui.tableNextColumn();
						ImGui.newLine();

						if (ImGui.button("%s##edit%s".formatted(IconFont.greasepencil, i), frameSize, frameSize))
							currentlyEditingSubject = subject;

						ImGui.sameLine();

						if (ImGui.button("%s##delete%s".formatted(IconFont.trash, i), frameSize, frameSize))
						{
							var choice = DialogUtil.notifyChoice(
									"Delete subject",
									"Are you sure you want to delete this subject?",
									DialogUtil.Icon.WARNING,
									DialogUtil.ButtonGroup.YESNO,
									false);

							if (choice == DialogUtil.Button.YES)
								iterator.remove();
						}
					});

					ImGui.endTable();
				}
				ImGui.endListBox();
			}

			ImGui.tableNextColumn();

			renderEditor(project);

			ImGui.endTable();
		}
	}

	private static void renderEditor(Project project)
	{
		if (currentlyEditingSubject == null)
			return;

		if (ImGui.beginTable("subjectEditor", 2))
		{
			ImGui.tableSetupColumn("label", ImGuiTableColumnFlags.WidthFixed, 100);
			ImGui.tableSetupColumn("field", ImGuiTableColumnFlags.WidthStretch);

			ImGui.tableNextColumn();
			ImGui.text("Subject ID");
			ImGui.tableNextColumn();
			ImGui.textDisabled(currentlyEditingSubject.getId());

			ImGui.tableNextColumn();
			ImGui.text("Age");
			ImGui.tableNextColumn();

			var age = new ImInt(currentlyEditingSubject.getAge());
			if (ImGui.inputInt("##subjectAge", age))
				currentlyEditingSubject.setAge(age.get());

			ImGui.tableNextColumn();
			ImGui.text("Gender");
			ImGui.tableNextColumn();

			if (ImGui.beginCombo("##subjectGender", currentlyEditingSubject.getGender().getName()))
			{
				for (var gender : genders)
				{
					if (ImGui.selectable(gender.getName()))
						currentlyEditingSubject.setGender(gender);
				}

				ImGui.endCombo();
			}

			ImGui.endTable();
		}
	}
}
