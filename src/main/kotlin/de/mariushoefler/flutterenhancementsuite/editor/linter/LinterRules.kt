package de.mariushoefler.flutterenhancementsuite.editor.linter

import de.mariushoefler.flutterenhancementsuite.models.LinterRule

val rules = arrayListOf(
    LinterRule(
        "always_declare_return_types",
        "Declare method return types."
    ),
    LinterRule(
        "always_put_control_body_on_new_line ",
        " Separate the control structure expression from its statement."
    ),
    LinterRule(
        "always_put_required_named_parameters_first",
        "Put @required named parameters first."
    ),
    LinterRule(
        "always_require_non_null_named_parameters ",
        " Use @required. "
    ),
    LinterRule(
        "always_specify_types",
        "Specify type annotations."
    ),
    LinterRule(
        " annotate_overrides ",
        " Annotate overridden members ."
    ),
    LinterRule(
        "avoid_annotating_with_dynamic",
        "Avoid annotating with dynamic when not required."
    ),
    LinterRule(
        " avoid_as ",
        " Avoid using as."
    ),
    LinterRule(
        "avoid_bool_literals_in_conditional_expressions",
        "Avoid bool literals in conditional expressions."
    ),
    LinterRule(
        "avoid_catches_without_on_clauses",
        "Avoid catches without on clauses."
    ),
    LinterRule(
        "avoid_catching_errors",
        "Don't explicitly catch Error or types that implement it."
    ),
    LinterRule(
        "avoid_classes_with_only_static_members",
        "Avoid defining a class that contains only static members."
    ),
    LinterRule(
        "avoid_double_and_int_checks",
        "Avoid double and int checks."
    ),
    LinterRule(
        "avoid_empty_else",
        "Avoid empty else statements."
    ),
    LinterRule(
        "avoid_equals_and_hash_code_on_mutable_classes",
        "AVOID overloading operator == and hashCode on classes not marked @immutable."
    ),
    LinterRule(
        "avoid_field_initializers_in_const_classes",
        "Avoid field initializers in const classes."
    ),
    LinterRule(
        "avoid_function_literals_in_foreach_calls",
        "Avoid using forEach with a function literal."
    ),
    LinterRule(
        "avoid_implementing_value_types",
        "Don't implement classes that override ==."
    ),
    LinterRule(
        "avoid_init_to_null",
        "Don't explicitly initialize variables to null."
    ),
    LinterRule(
        "avoid_js_rounded_ints",
        "Avoid JavaScript rounded ints."
    ),
    LinterRule(
        "avoid_null_checks_in_equality_operators",
        "Don't check for null in custom == operators."
    ),
    LinterRule(
        "avoid_positional_boolean_parameters",
        "Avoid positional boolean parameters."
    ),
    LinterRule(
        "avoid_print",
        "Avoid print calls in production code."
    ),
    LinterRule(
        "avoid_private_typedef_functions",
        "Avoid private typedef functions."
    ),
    LinterRule(
        "avoid_relative_lib_imports",
        "Avoid relative imports for files in lib/."
    ),
    LinterRule(
        "avoid_renaming_method_parameters",
        "Don't rename parameters of overridden methods."
    ),
    LinterRule(
        "avoid_return_types_on_setters",
        "Avoid return types on setters."
    ),
    LinterRule(
        "avoid_returning_null",
        "Avoid returning null from members whose return type is bool, double, int, or num."
    ),
    LinterRule(
        "avoid_returning_null_for_future",
        "Avoid returning null for Future."
    ),
    LinterRule(
        "avoid_returning_null_for_void",
        "Avoid returning null for void."
    ),
    LinterRule(
        "avoid_returning_this",
        "Avoid returning this from methods just to enable a fluent interface."
    ),
    LinterRule(
        "avoid_setters_without_getters",
        "Avoid setters without getters."
    ),
    LinterRule(
        "avoid_shadowing_type_parameters",
        "Avoid shadowing type parameters."
    ),
    LinterRule(
        "avoid_single_cascade_in_expression_statements",
        "Avoid single cascade in expression statements."
    ),
    LinterRule(
        "avoid_slow_async_io",
        "Avoid slow async dart:io methods."
    ),
    LinterRule(
        "avoid_types_as_parameter_names",
        "Avoid types as parameter names."
    ),
    LinterRule(
        "avoid_types_on_closure_parameters",
        "Avoid annotating types for function expression parameters."
    ),
    LinterRule(
        "avoid_unused_constructor_parameters",
        "Avoid defining unused parameters in constructors."
    ),
    LinterRule(
        "avoid_void_async",
        "Avoid async functions that return void."
    ),
    LinterRule(
        "await_only_futures",
        "Await only futures."
    ),
    LinterRule(
        "camel_case_extensions",
        "Name extensions using UpperCamelCase."
    ),
    LinterRule(
        "camel_case_types",
        "Name types using UpperCamelCase."
    ),
    LinterRule(
        "cancel_subscriptions",
        "Cancel instances of dart.async.StreamSubscription."
    ),
    LinterRule(
        "cascade_invocations",
        "Cascade consecutive method invocations on the same reference."
    ),
    LinterRule(
        "close_sinks",
        "Close instances of dart.core.Sink."
    ),
    LinterRule(
        "comment_references",
        "Only reference in scope identifiers in doc comments."
    ),
    LinterRule(
        "constant_identifier_names",
        "Prefer using lowerCamelCase for constant names."
    ),
    LinterRule(
        "control_flow_in_finally",
        "Avoid control flow in finally blocks."
    ),
    LinterRule(
        "curly_braces_in_flow_control_structures",
        "DO use curly braces for all flow control structures."
    ),
    LinterRule(
        "diagnostic_describe_all_properties",
        "DO reference all public properties in debug methods. (experimental)"
    ),
    LinterRule(
        "directives_ordering",
        "Adhere to Effective Dart Guide directives sorting conventions."
    ),
    LinterRule(
        "empty_catches",
        "Avoid empty catch blocks."
    ),
    LinterRule(
        "empty_constructor_bodies",
        "Use ; instead of {} for empty constructor bodies."
    ),
    LinterRule(
        "empty_statements",
        "Avoid empty statements."
    ),
    LinterRule(
        "file_names",
        "Name source files using lowercase_with_underscores."
    ),
    LinterRule(
        "flutter_style_todos",
        "Use Flutter TODO format: // TODO(username): message, https://URL-to-issue."
    ),
    LinterRule(
        "hash_and_equals",
        "Always override hashCode if overriding ==."
    ),
    LinterRule(
        "implementation_imports",
        "Don't import implementation files from another package."
    ),
    LinterRule(
        "invariant_booleans",
        "Conditions should not unconditionally evaluate to true or to false. (experimental)"
    ),
    LinterRule(
        "iterable_contains_unrelated_type",
        "Invocation of Iterable.contains with references of unrelated types."
    ),
    LinterRule(
        "join_return_with_assignment",
        "Join return statement with assignment when possible."
    ),
    LinterRule(
        "library_names",
        "Name libraries using lowercase_with_underscores."
    ),
    LinterRule(
        "library_prefixes",
        "Use lowercase_with_underscores when specifying a library prefix."
    ),
    LinterRule(
        "lines_longer_than_80_chars",
        "AVOID lines longer than 80 characters."
    ),
    LinterRule(
        "list_remove_unrelated_type",
        "Invocation of remove with references of unrelated types."
    ),
    LinterRule(
        "literal_only_boolean_expressions",
        "Boolean expression composed only with literals."
    ),
    LinterRule(
        "no_adjacent_strings_in_list",
        "Don't use adjacent strings in list."
    ),
    LinterRule(
        "no_duplicate_case_values",
        "Don't use more than one case with same value."
    ),
    LinterRule(
        "non_constant_identifier_names",
        "Name non-constant identifiers using lowerCamelCase."
    ),
    LinterRule(
        "null_closures",
        "Do not pass null as an argument where a closure is expected."
    ),
    LinterRule(
        "omit_local_variable_types",
        "Omit type annotations for local variables."
    ),
    LinterRule(
        "one_member_abstracts",
        "Avoid defining a one-member abstract class when a simple function will do."
    ),
    LinterRule(
        "only_throw_errors",
        "Only throw instances of classes extending either Exception or Error."
    ),
    LinterRule(
        "overridden_fields",
        "Don't override fields."
    ),
    LinterRule(
        "package_api_docs",
        "Provide doc comments for all public APIs."
    ),
    LinterRule(
        "package_names",
        "Use lowercase_with_underscores for package names."
    ),
    LinterRule(
        "package_prefixed_library_names",
        "Prefix library names with the package name and a dot-separated path."
    ),
    LinterRule(
        "parameter_assignments",
        "Don't reassign references to parameters of functions or methods."
    ),
    LinterRule(
        "prefer_adjacent_string_concatenation",
        "Use adjacent strings to concatenate string literals."
    ),
    LinterRule(
        "prefer_asserts_in_initializer_lists",
        "Prefer putting asserts in initializer list."
    ),
    LinterRule(
        "prefer_asserts_with_message",
        "Prefer asserts with message."
    ),
    LinterRule(
        "prefer_collection_literals",
        "Use collection literals when possible."
    ),
    LinterRule(
        "prefer_conditional_assignment",
        "Prefer using ??= over testing for null."
    ),
    LinterRule(
        "prefer_const_constructors",
        "Prefer const with constant constructors."
    ),
    LinterRule(
        "prefer_const_constructors_in_immutables",
        "Prefer declare const constructors on @immutable classes."
    ),
    LinterRule(
        "prefer_const_declarations",
        "Prefer const over final for declarations."
    ),
    LinterRule(
        "prefer_const_literals_to_create_immutables",
        "Prefer const literals as parameters of constructors on @immutable classes."
    ),
    LinterRule(
        "prefer_constructors_over_static_methods",
        "Prefer defining constructors instead of static methods to create instances."
    ),
    LinterRule(
        "prefer_contains",
        "Use contains for List and String instances."
    ),
    LinterRule(
        "prefer_double_quotes",
        "Prefer double quotes where they won't require escape sequences."
    ),
    LinterRule(
        "prefer_equal_for_default_values",
        "Use = to separate a named parameter from its default value."
    ),
    LinterRule(
        "prefer_expression_function_bodies",
        "Use => for short members whose body is a single return statement."
    ),
    LinterRule(
        "prefer_final_fields",
        "Private field could be final."
    ),
    LinterRule(
        "prefer_final_in_for_each",
        "Prefer final in for-each loop variable if reference is not reassigned."
    ),
    LinterRule(
        "prefer_final_locals",
        "Prefer final for variable declarations if they are not reassigned."
    ),
    LinterRule(
        "prefer_for_elements_to_map_fromIterable",
        "Prefer for elements when building maps from iterables."
    ),
    LinterRule(
        "prefer_foreach",
        "Use forEach to only apply a function to all the elements."
    ),
    LinterRule(
        "prefer_function_declarations_over_variables",
        "Use a function declaration to bind a function to a name."
    ),
    LinterRule(
        "prefer_generic_function_type_aliases",
        "Prefer generic function type aliases."
    ),
    LinterRule(
        "prefer_if_elements_to_conditional_expressions",
        "Prefer if elements to conditional expressions where possible."
    ),
    LinterRule(
        "prefer_if_null_operators",
        "Prefer using if null operators."
    ),
    LinterRule(
        "prefer_initializing_formals",
        "Use initializing formals when possible."
    ),
    LinterRule(
        "prefer_inlined_adds",
        "Inline list item declarations where possible."
    ),
    LinterRule(
        "prefer_int_literals",
        "Prefer int literals over double literals."
    ),
    LinterRule(
        "prefer_interpolation_to_compose_strings",
        "Use interpolation to compose strings and values."
    ),
    LinterRule(
        "prefer_is_empty",
        "Use isEmpty for Iterables and Maps."
    ),
    LinterRule(
        "prefer_is_not_empty",
        "Use isNotEmpty for Iterables and Maps."
    ),
    LinterRule(
        "prefer_iterable_whereType",
        "Prefer to use whereType on iterable."
    ),
    LinterRule(
        "prefer_mixin",
        "Prefer using mixins."
    ),
    LinterRule(
        "prefer_null_aware_operators",
        "Prefer using null aware operators."
    ),
    LinterRule(
        "prefer_single_quotes",
        "Prefer single quotes where they won't require escape sequences."
    ),
    LinterRule(
        "prefer_spread_collections",
        "Use spread collections when possible."
    ),
    LinterRule(
        "prefer_typing_uninitialized_variables",
        "Prefer typing uninitialized variables and fields."
    ),
    LinterRule(
        "prefer_void_to_null",
        "Don't use the Null type, unless you are positive that you don't want void."
    ),
    LinterRule(
        "provide_deprecation_message",
        "Provide a deprecation message, via @Deprecated(\"message\")."
    ),
    LinterRule(
        "public_member_api_docs",
        "Document all public members."
    ),
    LinterRule(
        "recursive_getters",
        "Property getter recursively returns itself."
    ),
    LinterRule(
        "slash_for_doc_comments",
        "Prefer using /// for doc comments."
    ),
    LinterRule(
        "sort_child_properties_last",
        "Sort child properties last in widget instance creations."
    ),
    LinterRule(
        "sort_constructors_first",
        "Sort constructor declarations before other members."
    ),
    LinterRule(
        "sort_pub_dependencies",
        "Sort pub dependencies."
    ),
    LinterRule(
        "sort_unnamed_constructors_first",
        "Sort unnamed constructor declarations first."
    ),
    LinterRule(
        "test_types_in_equals",
        "Test type arguments in operator ==(Object other)."
    ),
    LinterRule(
        "throw_in_finally",
        "Avoid throw in finally block."
    ),
    LinterRule(
        "type_annotate_public_apis",
        "Type annotate public APIs."
    ),
    LinterRule(
        "type_init_formals",
        "Don't type annotate initializing formals."
    ),
    LinterRule(
        "unawaited_futures",
        "Future results in async function bodies must be awaited or marked unawaited using package:pedantic."
    ),
    LinterRule(
        "unnecessary_await_in_return",
        "Unnecessary await keyword in return."
    ),
    LinterRule(
        "unnecessary_brace_in_string_interps",
        "Avoid using braces in interpolation when not needed."
    ),
    LinterRule(
        "unnecessary_const",
        "Avoid const keyword."
    ),
    LinterRule(
        "unnecessary_getters_setters",
        "Avoid wrapping fields in getters and setters just to be \"safe\"."
    ),
    LinterRule(
        "unnecessary_lambdas",
        "Don't create a lambda when a tear-off will do."
    ),
    LinterRule(
        "unnecessary_new",
        "Unnecessary new keyword."
    ),
    LinterRule(
        "unnecessary_null_aware_assignments",
        "Avoid null in null-aware assignment."
    ),
    LinterRule(
        "unnecessary_null_in_if_null_operators",
        "Avoid using null in if null operators."
    ),
    LinterRule(
        "unnecessary_overrides",
        "Don't override a method to do a super method invocation with the same parameters."
    ),
    LinterRule(
        "unnecessary_parenthesis",
        "Unnecessary parenthesis can be removed."
    ),
    LinterRule(
        "unnecessary_statements",
        "Avoid using unnecessary statements."
    ),
    LinterRule(
        "unnecessary_this",
        "Don't access members with this unless avoiding shadowing."
    ),
    LinterRule(
        "unrelated_type_equality_checks",
        "Equality operator == invocation with references of unrelated types."
    ),
    LinterRule(
        "unsafe_html",
        "\$_descPrefix."
    ),
    LinterRule(
        "use_full_hex_values_for_flutter_colors",
        "Prefer an 8-digit hexadecimal integer(0xFFFFFFFF) to instantiate Color."
    ),
    LinterRule(
        "use_function_type_syntax_for_parameters",
        "Use generic function type syntax for parameters."
    ),
    LinterRule(
        "use_rethrow_when_possible",
        "Use rethrow to rethrow a caught exception."
    ),
    LinterRule(
        "use_setters_to_change_properties",
        "Use a setter for operations that conceptually change a property."
    ),
    LinterRule(
        "use_string_buffers",
        "Use string buffers to compose strings."
    ),
    LinterRule(
        "use_to_and_as_if_applicable",
        "Start the name of the method with to/_to or as/_as if applicable."
    ),
    LinterRule(
        "valid_regexps",
        "Use valid regular expression syntax."
    ),
    LinterRule(
        "void_checks",
        "Don't assign to void."
    )
)
