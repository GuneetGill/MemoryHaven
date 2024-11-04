MemoryHaven Project Code Style Guide

Naming Conventions
Class names use PascalCase (e.g., ArchiveActivity, DataClass, MainActivity, MyAdapter, ProfileActivity, SecondActivity, UploadActivity). Method names use camelCase (e.g., loadData, initializeView, handleClick). Variables use camelCase with descriptive names (e.g., recyclerView, dataList, userName). Constants are in UPPERCASE with underscores (e.g., MAX_ITEM_COUNT, DEFAULT_IMAGE_PATH). XML layout files use lowercase with underscores (e.g., activity_main.xml, grid_item.xml).

Formatting and Structure
Indentation is 4 spaces per level. Avoid tabs for consistency. Line length is limited to 100 characters. Control statements use K&R style: the opening brace is on the same line, and the closing brace aligns with the statement start. Use a single blank line between methods and add spaces around operators (e.g., +, -, =).

Comments and Documentation
Comments must be added whenever there is code that is unclear or complex.Excessive commenting should be avoided.

Error handling:
Use toast for user-friendly feedback on successful or unsuccessful operations like image upload

Project Structure and Documentation
Group related files by functionality. Place all adapters in an "adapters" package, activities in an "activities" package, and ensure layout files are named descriptively.

Indentation and formatting:
Braces should be placed on the same line as
the statement (e.g., public class HomeFragment extends Fragment {)
Limit lines of code to 120 characters or fewer.

Other:
Text should be placed in strings.xml and referenced using @string.