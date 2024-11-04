MemoryHaven Project Code Style Guide

Naming Conventions
Class names use PascalCase (e.g., ArchiveActivity, DataClass, MainActivity, MyAdapter, ProfileActivity, SecondActivity, UploadActivity). Method names use camelCase (e.g., loadData, initializeView, handleClick). Variables use camelCase with descriptive names (e.g., recyclerView, dataList, userName). Constants are in UPPERCASE with underscores (e.g., MAX_ITEM_COUNT, DEFAULT_IMAGE_PATH). XML layout files use lowercase with underscores (e.g., activity_main.xml, grid_item.xml).

Formatting and Structure
Indentation is 4 spaces per level. Avoid tabs for consistency. Line length is limited to 100 characters. Control statements use K&R style: the opening brace is on the same line, and the closing brace aligns with the statement start. Use a single blank line between methods and add spaces around operators (e.g., +, -, =).

Comments and Documentation
File headers contain a brief description, author information, and creation date (e.g., // ArchiveActivity.java: Displays archived items for the user. Author: Julian Loewenherz. Date: November 4, 2024). Classes have a brief description of purpose. Public methods use JavaDoc comments describing purpose, parameters, and return values (e.g., Loads data from Firebase and updates RecyclerView. @param userId: unique ID of the user. @return boolean for success). Inline comments are used sparingly to clarify complex logic.

Git Workflow
Create a feature branch for each new feature or bug fix (e.g., feature/upload-functionality, bugfix/image-upload). Commit messages are clear, concise, and use the present tense (e.g., Add feature to upload images, Fix issue with memory list sorting). All pull requests should be reviewed by at least one team member before merging.

Project Structure and Documentation
Group related files by functionality. Place all adapters in an "adapters" package, activities in an "activities" package, and ensure layout files are named descriptively.

