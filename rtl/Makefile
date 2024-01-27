CPU     ?= x
CPU_LIST = me32ls me32lm me32lp me32ep2

ifeq ($(filter $(CPU_LIST), $(CPU)),)
    ifeq ($(findstring $(MAKECMDGOALS), config|clean),)
        $(error $$CPU is incorrect, optional values in [$(CPU_LIST)])
    endif
endif

CPU_PREFIX = m$(shell echo $(CPU) | cut -c5-5).$(CPU)

CPU_BLACKLIST = $(filter-out $(CPU), $(CPU_LIST))
CPU_BLACKLIT_PREFIX =
$(foreach str, $(CPU_BLACKLIST), \
    $(eval CPU_BLACKLIT_PREFIX += m$(shell echo $(str) | cut -c5-5)/$(str)))

CPU_HOME = $(abspath ./)

# ifeq ($(wildcard $(CPU_HOME)/emu/src/main.c),)
#     $(error CPU_HOME=$(CPU_HOME) is not a NPC repo)
# endif

# -include $(CPU_HOME)/emu/include/config/auto.conf
# -include $(CPU_HOME)/emu/include/config/auto.conf.cmd

export PATH := $(PATH):$(abspath ./util)

# FUNC_REMOVE_QUOTE = $(patsubst "%",%,$(1))

# CFG_CFLAGS_TOP_NAME   = Top
# CFG_GUEST_ISA ?= $(call FUNC_REMOVE_QUOTE, $(CONFIG_ISA))
# CFG_ENGINE    ?= $(call FUNC_REMOVE_QUOTE, $(CONFIG_ENGINE))

DIR_BUILD = $(CPU_HOME)/cpu/build
DIR_OBJ   = $(DIR_BUILD)/obj_dir

# NPC_BIN_NAME = $(CFG_GUEST_ISA)-npc-$(CFG_ENGINE)
# NPC_BIN      = $(DIR_BUILD)/$(NPC_BIN_NAME)

# VERILATOR         = verilator
# VERILATOR_CFLAGS += -MMD --build -cc                                \
#                     -O3 --x-assign fast --x-initial fast --noassert \
#                     --trace                                         \
#                     +incdir+$(CPU_HOME)/cpu/srcv

# INCS_DIR = $(CPU_HOME)/emu/include           \
#            $(CPU_HOME)/emu/include/generated
# INCS     = $(shell find $(INCS_DIR) -name "*.h")
# INCFLAGS = $(addprefix -I, $(INCS_DIR))

# CFLAGS_BUILD += $(call FUNC_REMOVE_QUOTE, $(CONFIG_CC_OPT))
# CFLAGS_BUILD += $(if $(CONFIG_CC_LTO), -flto,)
# CFLAGS_BUILD += $(if $(CONFIG_CC_DEBUG), -Og -ggdb3,)
# CFLAGS_BUILD += $(if $(CONFIG_CC_ASAN), -fsanitize=address,)
# CFLAGS_BUILD += -DCFLAGS_CPU_TYPE=$(CPU)
# ifeq ($(CPU), single)
#     CFLAGS_BUILD += -DCFLAGS_CPU_TYPE_SINGLE
# endif
# ifeq ($(CPU), multip)
#     CFLAGS_BUILD += -DCFLAGS_CPU_TYPE_MULTIP
# endif
# ifeq ($(CPU), pipeline)
#     CFLAGS_BUILD += -DCFLAGS_CPU_TYPE_PIPELINE
# endif
# CFLAGS       += $(CFLAGS_BUILD)                                 \
#                 -DCFLAGS_GUEST_ISA=$(CFG_GUEST_ISA)             \
#                 -Wno-unused-result                              \
#                 -DCFLAGS_TOP_NAME="\"V$(CFG_CFLAGS_TOP_NAME)\"" \
#                 $(INCFLAGS)                                     \
#                 $(shell llvm-config-11 --cxxflags) -fPIE

# LDFLAGS += $(CFLAGS_BUILD)                \
#            -lreadline -ldl -pie           \
#            $(shell llvm-config-11 --libs) \
#            -lSDL2

SRCSS_DIR           = $(abspath $(CPU_HOME)/cpu/src)
SRCSS_SRC_BLACKLIST =
SRCSS_DIR_BLACKLIST = $(addprefix $(SRCSS_DIR)/core/, $(CPU_BLACKLIT_PREFIX))
SRCSS_BLACKLIST     = $(SRCSS_SRC_BLACKLIST)                               \
                      $(shell find $(SRCSS_DIR_BLACKLIST) -name "*.scala")
SRCSS_WHITELIST     = $(shell find $(SRCSS_DIR) -name "*.scala")
SRCSS               = $(filter-out $(SRCSS_BLACKLIST), $(SRCSS_WHITELIST))

# SRCVS_DIR = $(abspath $(CPU_HOME)/cpu/srcv)
SRCVS_GEN = $(DIR_BUILD)/$(CFG_CFLAGS_TOP_NAME).v
# SRCVS     = $(shell find $(SRCVS_DIR) -name "*.v")

# SRCCS_DIR += $(CPU_HOME)/emu/src             \
#              $(CPU_HOME)/emu/src/cpu         \
#              $(CPU_HOME)/emu/src/debug       \
#              $(CPU_HOME)/emu/src/device      \
#              $(CPU_HOME)/emu/src/isa         \
#              $(CPU_HOME)/emu/src/memory      \
#              $(CPU_HOME)/emu/src/monitor     \
#              $(CPU_HOME)/emu/src/monitor/sdb \
#              $(CPU_HOME)/emu/src/utils
# SRCCS_SRC_BLACKLIST =
# SRCCS_DIR_BLACKLIST =
# SRCCS_BLACKLIST     = $(SRCCS_SRC_BLACKLIST)                               \
#                       $(shell find $(SRCCS_DIR_BLACKLIST) -name "*.c")
# SRCCS_WHITELIST     = $(shell find $(SRCCS_DIR) -name "*.c")
# SRCCS               = $(filter-out $(SRCCS_BLACKLIST), $(SRCCS_WHITELIST)) \
#                       $(CPU_HOME)/emu/src/utils/disasm.cc

# SRCS = $(SRCVS_GEN) $(SRCVS) $(SRCCS)

# CONFIG = $(CPU_HOME)/emu/.config

BUILD_PATH = $(SRCSS_DIR)/temp/Build.scala
BUILD_INFO = "package cpu.temp\n\ntrait Build {\n    val CPU_TYPE = \"$(CPU)\"\n}"

TOP_MAIN_PATH = $(SRCSS_DIR)/temp/TopMain.scala
TOP_MAIN_INFO_L1 = "import circt.stage._\n"
TOP_MAIN_INFO_L2 = "object TopMain extends App {"
TOP_MAIN_INFO_L3 = "    def top = new cpu.core.${CPU_PREFIX}.Top();"
TOP_MAIN_INFO_L4 = "    val gen = Seq(chisel3.stage.ChiselGeneratorAnnotation(() => top));"
TOP_MAIN_INFO_L5 = "    (new ChiselStage).execute("
TOP_MAIN_INFO_L6 = "        args,"
TOP_MAIN_INFO_L7 = "        gen :+ CIRCTTargetAnnotation(CIRCTTarget.Verilog))"
TOP_MAIN_INFO_L8 = "}"

ifeq ($(shell cat $(BUILD_PATH) | grep $(CPU)),)
    $(shell rm -rf out)
    $(shell rm -rf cpu/build)
    $(shell rm -rf cpu/test_run_dir)
endif

$(SRCVS_GEN): $(SRCSS)
	mkdir -p $(SRCSS_DIR)/temp
	echo $(BUILD_INFO) > $(BUILD_PATH) &&             \
	echo $(TOP_MAIN_INFO_L1)  > $(TOP_MAIN_PATH) &&   \
	echo $(TOP_MAIN_INFO_L2) >> $(TOP_MAIN_PATH) &&   \
	echo $(TOP_MAIN_INFO_L3) >> $(TOP_MAIN_PATH) &&   \
	echo $(TOP_MAIN_INFO_L4) >> $(TOP_MAIN_PATH) &&   \
	echo $(TOP_MAIN_INFO_L5) >> $(TOP_MAIN_PATH) &&   \
	echo $(TOP_MAIN_INFO_L6) >> $(TOP_MAIN_PATH) &&   \
	echo $(TOP_MAIN_INFO_L7) >> $(TOP_MAIN_PATH) &&   \
	echo $(TOP_MAIN_INFO_L8) >> $(TOP_MAIN_PATH) &&   \
	mkdir -p $(DIR_BUILD) &&                          \
	mill -i cpu.test.runMain TopMain -td $(DIR_BUILD)
# $(NPC_BIN): $(INCS) $(SRCS) $(CONFIG)
# 	@rm -rf $(DIR_OBJ)
# 	$(VERILATOR) $(VERILATOR_CFLAGS)            \
# 	--top-module $(CFG_CFLAGS_TOP_NAME) $(SRCS) \
# 	$(addprefix -CFLAGS ,  $(CFLAGS))           \
# 	$(addprefix -LDFLAGS , $(LDFLAGS))          \
# 	--Mdir $(DIR_OBJ) --exe -o                  \
# 	$(abspath $(NPC_BIN))

# ifdef CONFIG_DIFFTEST
#     ARGS_EMBED_DIFF = --diff=$(NEMU_HOME)/build/riscv64-nemu-interpreter-so
# endif

# override ARGS_EMBED ?= --log=$(DIR_BUILD)/npc-log.txt
# override ARGS_EMBED += $(ARGS_EMBED_DIFF)

# IMG ?=
# NPC_EXEC := $(NPC_BIN) $(ARGS_EMBED) $(ARGS) $(IMG)

.PHONY: config test gen run gdb sim fmt cfmt clean bsp

# config:
# 	cd emu && make menuconfig

test:
	mill -i cpu.test

gen: $(SRCVS_GEN)

# run: $(NPC_BIN)
# 	rm -rf $(DIR_BUILD)/wave.vcd
# 	$(NPC_EXEC)

# gdb: $(NPC_BIN)
# 	rm -rf $(DIR_BUILD)/wave.vcd
# 	gdb -s $(NPC_BIN) --ARGS_EMBED $(NPC_EXEC)

sim: $(NPC_BIN)
	gtkwave $(DIR_BUILD)/wave.vcd

fmt:
	mill -i cpu.reformat

cfmt:
	mill -i cpu.checkFormat

clean:
	rm -rf out
	rm -rf cpu/build
	rm -rf cpu/test_run_dir

bsp:
	mill -i mill.bsp.BSP/install
